package com.cheestree.vetly.service

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
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
    private val animalRepository: AnimalRepository
) {
    private val MAX_PAGE_SIZE = 20

    fun getAllAnimals(
        name: String? = null,
        chip: String? = null,
        birth: OffsetDateTime? = null,
        breed: String? = null,
        owned: Boolean? = false,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<AnimalPreview> {
        val pageable: Pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(MAX_PAGE_SIZE),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<Animal>(
            { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%$it%") } },
            { root, cb -> chip?.let { cb.equal(root.get<String>("chip"), it) } },
            { root, cb -> birth?.let { cb.equal(root.get<OffsetDateTime>("birth"), it) } },
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
        veterinarian: AuthenticatedUser,
        name: String,
        chip: String?,
        birth: OffsetDateTime?,
        breed: String?,
        imageUrl: String?
    ): AnimalInformation {
        chip?.let {
            if(animalRepository.findAnimalByChip(chip).isNotEmpty()) {
                throw ResourceNotFoundException("Animal with chip $chip already exists")
            }
        }

        val animal = Animal(
            name = name,
            chip = chip,
            birth = birth,
            breed = breed,
            imageUrl = imageUrl
        )

        return animalRepository.save(animal).asPublic()
    }

    fun getAnimal(animalId: Long): AnimalInformation {
        return animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }.asPublic()
    }

    fun updateAnimal(
        animalId: Long,
        name: String?,
        chip: String?,
        birth: OffsetDateTime?,
        breed: String?,
        imageUrl: String?
    ): AnimalInformation {
        chip?.let {
            if(animalRepository.findAnimalByChip(chip).isNotEmpty()) {
                throw ResourceNotFoundException("Animal with chip $chip already exists")
            }
        }

        val animal = animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }

        val updatedAnimal = animal.copy(
            name = name ?: animal.name,
            chip = chip ?: animal.chip,
            birth = birth ?: animal.birth,
            breed = breed ?: animal.breed,
            imageUrl = imageUrl ?: animal.imageUrl
        )

        return animalRepository.save(updatedAnimal).asPublic()
    }

    fun deleteAnimal(animalId: Long): Boolean {
        val animal = animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }

        return animalRepository.deleteAnimalById(animal.id)
    }
}
