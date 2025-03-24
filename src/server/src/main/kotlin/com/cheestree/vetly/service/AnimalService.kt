package com.cheestree.vetly.service

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.http.model.output.animal.AnimalPreview
import com.cheestree.vetly.repository.AnimalRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AnimalService(
    private val animalRepository: AnimalRepository
) {
    fun getAllAnimals(): List<AnimalPreview> {
        return animalRepository.findAll().map { it.asPreview() }
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
        val animal = animalRepository.findById(animalId)
            .orElseThrow { ResourceNotFoundException("Animal with id $animalId not found") }

        chip?.let {
            if(animalRepository.findAnimalByChip(chip).isNotEmpty()) {
                throw ResourceNotFoundException("Animal with chip $chip already exists")
            }
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

        animalRepository.delete(animal)

        return true
    }
}
