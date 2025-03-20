package com.cheestree.vetly.services

import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.pet.animal.Animal
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.repository.AnimalRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AnimalService(
    private val animalRepository: AnimalRepository
) {
    fun getAllAnimals(): List<Animal> {
        return animalRepository.findAll()
    }

    fun createAnimal(
        veterinarian: AuthenticatedUser,
        name: String,
        chip: String?,
        birth: OffsetDateTime?,
        breed: String?,
        imageUrl: String?
    ): Animal {
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

        return animalRepository.save(animal)
    }

    fun getAnimal(animalId: Long): Animal {
        return animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }
    }

    fun updateAnimal(
        animalId: Long,
        name: String?,
        chip: String?,
        birth: OffsetDateTime?,
        breed: String?,
        imageUrl: String?
    ): Animal {
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

        return animalRepository.save(updatedAnimal)
    }

    fun deleteAnimal(animalId: Long): Boolean {
        val animal = animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }

        animalRepository.delete(animal)

        return true
    }
}
