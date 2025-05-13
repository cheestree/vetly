package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import java.time.OffsetDateTime
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class AnimalService(
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository,
    private val appConfig: AppConfig,
) {
    fun getAllAnimals(
        user: AuthenticatedUser,
        userId: Long? = null,
        name: String? = null,
        microchip: String? = null,
        birthDate: OffsetDateTime? = null,
        species: String? = null,
        owned: Boolean? = null,
        self: Boolean? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<AnimalPreview> {
        val pageable: Pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val resolvedUserId = when {
            user.roles.contains(Role.ADMIN) || user.roles.contains(Role.ADMIN) -> userId
            else -> user.id
        }

        val specs = withFilters<Animal>(
            { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%") } },
            { root, cb -> microchip?.let { cb.equal(root.get<String>("microchip"), it) } },
            { root, cb -> birthDate?.let { cb.equal(root.get<OffsetDateTime>("birthDate"), it) } },
            { root, cb -> species?.let { cb.like(cb.lower(root.get("species")), "%${it.lowercase()}%") } },
            { root, cb ->
                owned?.let {
                    when (it) {
                        true -> cb.isNotNull(root.get<User>("owner"))
                        false -> cb.isNull(root.get<User>("owner"))
                    }
                }
            },
            { root, cb ->
                self?.let {
                    when (it) {
                        true -> cb.equal(root.get<User>("owner").get<Long>("id"), user.id)
                        false -> cb.notEqual(root.get<User>("owner").get<Long>("id"), user.id)
                    }
                }
            },
            { root, cb ->
                if (!user.roles.contains(Role.ADMIN) && !user.roles.contains(Role.VETERINARIAN)) {
                    resolvedUserId?.let { cb.equal(root.get<User>("owner").get<Long>("id"), it) }
                } else {
                    null
                }
            }
        )

        val pageResult = animalRepository.findAll(specs, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
    }

    fun getAnimal(animalId: Long): AnimalInformation {
        val animal =
            animalRepository.findById(animalId).orElseThrow {
                ResourceNotFoundException("Animal with id $animalId not found")
            }

        if (!animal.isActive) {
            throw UnauthorizedAccessException("Animal with $animalId is not active")
        }

        return animal.asPublic()
    }

    fun createAnimal(
        name: String,
        microchip: String?,
        birthDate: OffsetDateTime?,
        species: String?,
        imageUrl: String?,
        ownerId: Long?,
    ): Long {
        microchip?.let {
            if (animalRepository.existsAnimalByMicrochip(microchip)) {
                throw ResourceAlreadyExistsException("Animal with microchip $microchip already exists")
            }
        }

        val owner =
            ownerId?.let {
                userRepository.findById(it).orElseThrow {
                    ResourceNotFoundException("User with id $it not found")
                }
            }

        val animal =
            Animal(
                name = name,
                microchip = microchip,
                birthDate = birthDate,
                species = species,
                imageUrl = imageUrl,
                owner = owner,
            )

        owner?.let { animal.addOwner(it) }

        return animalRepository.save(animal).id
    }

    fun updateAnimal(
        id: Long,
        name: String?,
        microchip: String?,
        birthDate: OffsetDateTime?,
        species: String?,
        imageUrl: String?,
        ownerId: Long?,
    ): AnimalInformation {
        val animal =
            animalRepository.findById(id).orElseThrow {
                ResourceNotFoundException("Animal with id $id not found")
            }

        if (!animal.isActive) {
            throw UnauthorizedAccessException("Animal with $id is not active")
        }

        microchip?.let {
            if (it != animal.microchip && animalRepository.existsAnimalByMicrochip(it)) {
                throw ResourceAlreadyExistsException("Animal with microchip $it already exists")
            }
        }

        val updatedOwner =
            ownerId?.let {
                userRepository.findById(it).orElseThrow {
                    ResourceNotFoundException("User with id $it not found")
                }
            }

        if (updatedOwner != animal.owner) {
            updatedOwner?.let { animal.addOwner(it) } ?: animal.removeOwner()
        }

        animal.owner?.addAnimal(animal)

        animal.updateWith(name, microchip, birthDate, species, imageUrl, updatedOwner)

        return animalRepository.save(animal).asPublic()
    }

    fun deleteAnimal(id: Long): Boolean {
        val animal =
            animalRepository.findById(id).orElseThrow {
                ResourceNotFoundException("Animal with id $id not found")
            }

        animal.removeOwner()
        animal.isActive = false

        animalRepository.save(animal)

        return true
    }

    private fun Animal.addOwner(updatedOwner: User) {
        this.owner?.removeAnimal(this)

        this.owner = updatedOwner
        updatedOwner.addAnimal(this)

        userRepository.save(updatedOwner)
        animalRepository.save(this)
    }

    private fun Animal.removeOwner() {
        this.owner?.removeAnimal(this)
        this.owner = null

        if (this.owner != null) {
            userRepository.save(this.owner!!)
        }
        animalRepository.save(this)
    }
}
