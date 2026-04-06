package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.exception.VetException.InactiveResourceException
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType.ANIMAL
import com.cheestree.vetly.domain.exception.VetException.ResourceType.USER
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalQueryInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.repository.BaseSpecs.combineAll
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.repository.animal.AnimalRepository
import com.cheestree.vetly.repository.animal.AnimalSpecs
import com.cheestree.vetly.service.Utils.createResource
import com.cheestree.vetly.service.Utils.deleteResource
import com.cheestree.vetly.service.Utils.executeOperation
import com.cheestree.vetly.service.Utils.retrieveResource
import com.cheestree.vetly.service.Utils.updateResource
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AnimalService(
    private val animalRepository: AnimalRepository,
    private val userRepository: UserRepository,
    private val storageService: StorageService,
    private val appConfig: AppConfig,
) {
    @Transactional(readOnly = true)
    fun getAllAnimals(
        user: AuthenticatedUser,
        query: AnimalQueryInputModel = AnimalQueryInputModel(),
    ): ResponseList<AnimalPreview> {
        val pageable: Pageable =
            PageRequest.of(
                query.page.coerceAtLeast(0),
                query.size.coerceIn(1, appConfig.paging.maxPageSize),
                Sort.by(query.sortDirection, query.sortBy),
            )

        val specs =
            combineAll(
                AnimalSpecs.nameContains(query.name),
                AnimalSpecs.microchipEquals(query.microchip),
                AnimalSpecs.sexEquals(query.sex),
                AnimalSpecs.isSterile(query.sterilized),
                AnimalSpecs.speciesEquals(query.species),
                AnimalSpecs.bornIn(
                    query.startBirthdate,
                    query.endBirthdate,
                ),
                AnimalSpecs.hasOwner(query.owned),
                AnimalSpecs.isSelf(query.self, user.id),
                AnimalSpecs.byEmail(query.userEmail, user.roles, user.email),
                AnimalSpecs.isActive(query.active, user.roles),
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

    @Cacheable(cacheNames = ["animals"], key = "#id + ':' + #user.id")
    @Transactional(readOnly = true)
    fun getAnimal(
        user: AuthenticatedUser,
        id: Long,
    ): AnimalInformation =
        retrieveResource(ANIMAL, id) {
            val animal =
                animalRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ANIMAL, id)
                }

            if (!animal.isActive) {
                throw InactiveResourceException(ANIMAL, id)
            }

            val isVeterinarian = user.roles.contains(VETERINARIAN)
            val isOwner = animal.owner?.email == user.email

            if (!isVeterinarian && !isOwner) {
                throw ForbiddenException("You can only access your own animals")
            }

            animal.asPublic()
        }

    @Transactional(readOnly = true)
    fun getAnimal(id: Long): AnimalInformation =
        retrieveResource(ANIMAL, id) {
            val animal =
                animalRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ANIMAL, id)
                }

            if (!animal.isActive) {
                throw InactiveResourceException(ANIMAL, id)
            }

            animal.asPublic()
        }

    @Transactional
    fun createAnimal(
        createdAnimal: AnimalCreateInputModel,
        image: MultipartFile?,
    ): AnimalInformation =
        createResource(ANIMAL) {
            createdAnimal.microchip?.let {
                if (animalRepository.existsAnimalByMicrochip(createdAnimal.microchip)) {
                    throw ResourceAlreadyExistsException(ANIMAL, "microchip", createdAnimal.microchip)
                }
            }

            val owner =
                createdAnimal.ownerEmail?.let {
                    userRepository.findByEmail(it).orElseThrow {
                        ResourceNotFoundException(USER, it)
                    }
                }

            val animal =
                Animal(
                    name = createdAnimal.name,
                    microchip = createdAnimal.microchip,
                    sex = createdAnimal.sex,
                    sterilized = createdAnimal.sterilized,
                    birthDate = createdAnimal.birthDate,
                    species = createdAnimal.species,
                    image = null,
                    owner = owner,
                )

            val savedAnimal = animalRepository.save(animal)

            val uploadedImage =
                image?.let {
                    storageService.uploadFile(
                        file = it,
                        folder = StorageFolder.ANIMALS,
                        identifier = "${savedAnimal.id}",
                        customFileName = savedAnimal.name,
                    )
                }

            savedAnimal.image = uploadedImage
            owner?.let { savedAnimal.addOwner(it) }

            animalRepository.save(savedAnimal).asPublic()
        }

    @CacheEvict(cacheNames = ["animals"], allEntries = true)
    @Transactional
    fun updateAnimal(
        id: Long,
        updatedAnimal: AnimalUpdateInputModel,
        image: MultipartFile?,
    ): AnimalInformation =
        updateResource(ANIMAL, id) {
            val animal =
                animalRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ANIMAL, id)
                }

            if (!animal.isActive) {
                throw InactiveResourceException(ANIMAL, id)
            }

            updatedAnimal.microchip.ifPresent { microchip ->
                if (microchip != animal.microchip &&
                    microchip != null &&
                    animalRepository.existsAnimalByMicrochip(microchip)
                ) {
                    throw ResourceAlreadyExistsException(ANIMAL, "microchip", microchip)
                }
            }

            val updatedOwner =
                if (updatedAnimal.ownerEmail.isPresent) {
                    updatedAnimal.ownerEmail.get()?.let {
                        userRepository.findByEmail(it).orElseThrow {
                            ResourceNotFoundException(USER, it)
                        }
                    }
                } else {
                    null
                }

            if (updatedAnimal.ownerEmail.isPresent && updatedOwner != animal.owner) {
                updatedOwner?.let { animal.addOwner(it) } ?: animal.removeOwner()
            }

            animal.owner?.addAnimal(animal)

            image?.let {
                val newImage =
                    storageService.replaceFile(
                        oldFile = animal.image,
                        newFile = it,
                        folder = StorageFolder.ANIMALS,
                        identifier = "${animal.id}",
                        customFileName = animal.name,
                    )

                animal.image = newImage
            }

            animal.updateWith(
                name = updatedAnimal.name.orElse(animal.name),
                microchip = updatedAnimal.microchip.orElse(animal.microchip),
                sex = updatedAnimal.sex.orElse(animal.sex),
                sterilized = updatedAnimal.sterilized.orElse(animal.sterilized),
                birthDate = updatedAnimal.birthDate.orElse(animal.birthDate),
                species = updatedAnimal.species.orElse(animal.species),
                owner = if (updatedAnimal.ownerEmail.isPresent) updatedOwner else animal.owner,
            )

            animalRepository.save(animal).asPublic()
        }

    @CacheEvict(cacheNames = ["animals"], allEntries = true)
    @Transactional
    fun deleteAnimal(id: Long): Boolean =
        deleteResource(ANIMAL, id) {
            val animal =
                animalRepository.findById(id).orElseThrow {
                    ResourceNotFoundException(ANIMAL, id)
                }

            animal.removeOwner()
            animal.isActive = false

            animal.image?.let {
                storageService.deleteFile(it)
            }

            animalRepository.save(animal)

            true
        }

    private fun Animal.addOwner(updatedOwner: User) {
        executeOperation("update owner for", ANIMAL, this.id) {
            this.owner?.removeAnimal(this)

            this.owner = updatedOwner
            updatedOwner.addAnimal(this)

            userRepository.save(updatedOwner)
            animalRepository.save(this)
        }
    }

    private fun Animal.removeOwner() {
        executeOperation("remove owner from", ANIMAL, this.id) {
            this.owner?.removeAnimal(this)
            this.owner = null

            animalRepository.save(this)
        }
    }
}
