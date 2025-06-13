package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.animal.Sex
import com.cheestree.vetly.domain.exception.VetException.InactiveResourceException
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.executeOperation
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import com.cheestree.vetly.service.Utils.Companion.withFilters
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Locale
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
        sex: Sex? = null,
        sterilized: Boolean? = null,
        species: String? = null,
        birthDate: LocalDate? = null,
        owned: Boolean? = null,
        self: Boolean? = null,
        page: Int = 0,
        size: Int = appConfig.paging.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<AnimalPreview> {
        val pageable: Pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val resolvedUserId =
            when {
                user.roles.contains(Role.ADMIN) || user.roles.contains(Role.ADMIN) -> userId
                else -> user.id
            }

        val specs =
            withFilters<Animal>(
                { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%${it.lowercase(Locale.getDefault())}%") } },
                { root, cb -> microchip?.let { cb.equal(root.get<String>("microchip"), it) } },
                { root, cb -> sex?.let { cb.like(root.get("sex"), "%${it.name.lowercase(Locale.getDefault())}%") } },
                { root, cb -> sterilized?.let { cb.equal(root.get<Boolean>("sterilized"), it) } },
                { root, cb ->
                    birthDate?.let {
                        val start = it.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()
                        val end = it.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()
                        cb.between(root.get("birthDate"), start, end)
                    }
                },
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
                },
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

    fun getAnimal(animalId: Long): AnimalInformation =
        retrieveResource(ResourceType.ANIMAL, animalId) {
            val animal =
                animalRepository.findById(animalId).orElseThrow {
                    ResourceNotFoundException(ResourceType.ANIMAL, animalId)
                }

            if (!animal.isActive) {
                throw InactiveResourceException(ResourceType.ANIMAL, animalId)
            }

            animal.asPublic()
        }

    fun createAnimal(
        name: String,
        microchip: String?,
        sex: Sex,
        sterilized: Boolean,
        species: String?,
        birthDate: OffsetDateTime?,
        imageUrl: String?,
        ownerId: Long?,
    ): Long =
        createResource(ResourceType.ANIMAL) {
            microchip?.let {
                if (animalRepository.existsAnimalByMicrochip(microchip)) {
                    throw ResourceAlreadyExistsException(ResourceType.ANIMAL, "microchip", microchip)
                }
            }

            val owner =
                ownerId?.let {
                    userRepository.findById(it).orElseThrow {
                        ResourceNotFoundException(ResourceType.USER, it)
                    }
                }

            val animal =
                Animal(
                    name = name,
                    microchip = microchip,
                    sex = sex,
                    sterilized = sterilized,
                    birthDate = birthDate,
                    species = species,
                    imageUrl = imageUrl,
                    owner = owner,
                )

            owner?.let { animal.addOwner(it) }

            animalRepository.save(animal).id
        }

    fun updateAnimal(
        id: Long,
        name: String?,
        microchip: String?,
        sex: Sex?,
        sterilized: Boolean?,
        species: String?,
        birthDate: OffsetDateTime?,
        imageUrl: String?,
        ownerId: Long?,
    ): AnimalInformation =
        updateResource(ResourceType.ANIMAL, id) {
            val animal =
                animalRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ResourceType.ANIMAL, id)
                }

            if (!animal.isActive) {
                throw InactiveResourceException(ResourceType.ANIMAL, id)
            }

            microchip?.let {
                if (it != animal.microchip && animalRepository.existsAnimalByMicrochip(it)) {
                    throw ResourceAlreadyExistsException(ResourceType.ANIMAL, "microchip", it)
                }
            }

            val updatedOwner =
                ownerId?.let {
                    userRepository.findById(it).orElseThrow {
                        ResourceNotFoundException(ResourceType.USER, it)
                    }
                }

            if (updatedOwner != animal.owner) {
                updatedOwner?.let { animal.addOwner(it) } ?: animal.removeOwner()
            }

            animal.owner?.addAnimal(animal)
            animal.updateWith(name, microchip, sex, sterilized, birthDate, species, imageUrl, updatedOwner)
            animalRepository.save(animal).asPublic()
        }

    fun deleteAnimal(id: Long): Boolean =
        deleteResource(ResourceType.ANIMAL, id) {
            val animal =
                animalRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ResourceType.ANIMAL, id)
                }

            animal.removeOwner()
            animal.isActive = false

            animalRepository.save(animal)

            true
        }

    private fun Animal.addOwner(updatedOwner: User) {
        executeOperation("update owner for", ResourceType.ANIMAL, this.id) {
            this.owner?.removeAnimal(this)

            this.owner = updatedOwner
            updatedOwner.addAnimal(this)

            userRepository.save(updatedOwner)
            animalRepository.save(this)
        }
    }

    private fun Animal.removeOwner() {
        executeOperation("remove owner from", ResourceType.ANIMAL, this.id) {
            this.owner?.removeAnimal(this)
            this.owner = null

            if (this.owner != null) {
                userRepository.save(this.owner!!)
            }
            animalRepository.save(this)
        }
    }
}
