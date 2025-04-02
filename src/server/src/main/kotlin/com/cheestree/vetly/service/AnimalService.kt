package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AnimalService(
    private val animalRepository: AnimalRepository,
    private val appConfig: AppConfig
) {
    fun getAllAnimals(
        name: String? = null,
        microchip: String? = null,
        birthDate: OffsetDateTime? = null,
        breed: String? = null,
        owned: Boolean? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<AnimalPreview> {
        val pageable: Pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(appConfig.maxPageSize),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<Animal>(
            { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%$it%") } },
            { root, cb -> microchip?.let { cb.equal(root.get<String>("microchip"), it) } },
            { root, cb -> birthDate?.let { cb.equal(root.get<OffsetDateTime>("birthDate"), it) } },
            { root, cb -> breed?.let { cb.like(cb.lower(root.get("breed")), "%$it%") } },
            { root, cb ->
                when (owned) {
                    true -> cb.isNotNull(root.get<User>("owner"))
                    false -> cb.isNull(root.get<User>("owner"))
                    null -> null
                }
            }
        )

        return animalRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun createAnimal(
        name: String,
        microchip: String?,
        birthDate: OffsetDateTime?,
        breed: String?,
        imageUrl: String?
    ): Long {
        microchip?.let {
            if(animalRepository.existsAnimalByMicrochip(microchip)) {
                throw ResourceAlreadyExistsException("Animal with microchip $microchip already exists")
            }
        }

        val animal = Animal(
            name = name,
            microchip = microchip,
            birthDate = birthDate,
            breed = breed,
            imageUrl = imageUrl
        )

        return animalRepository.save(animal).id
    }

    fun getAnimal(animalId: Long): AnimalInformation {
        return animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }.asPublic()
    }

    fun updateAnimal(
        id: Long,
        name: String?,
        microchip: String?,
        birthDate: OffsetDateTime?,
        breed: String?,
        imageUrl: String?
    ): AnimalInformation {
        microchip?.let {
            if(animalRepository.existsAnimalByMicrochip(microchip)) {
                throw ResourceNotFoundException("Animal with microchip $microchip already exists")
            }
        }

        val animal = animalRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Animal with id $id not found")
        }

        val updatedAnimal = animal.copy(
            name = name ?: animal.name,
            microchip = microchip ?: animal.microchip,
            birthDate = birthDate ?: animal.birthDate,
            breed = breed ?: animal.breed,
            imageUrl = imageUrl ?: animal.imageUrl
        )

        return animalRepository.save(updatedAnimal).asPublic()
    }

    fun deleteAnimal(id: Long): Boolean {
        val animal = animalRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Animal with id $id not found")
        }

        return animalRepository.deleteAnimalById(animal.id)
    }
}
