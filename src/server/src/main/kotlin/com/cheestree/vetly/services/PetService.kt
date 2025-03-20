package com.cheestree.vetly.services

import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.pet.Pet
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.PetRepository
import com.cheestree.vetly.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class PetService(
    private val userRepository: UserRepository,
    private val petRepository: PetRepository,
    private val animalRepository: AnimalRepository
) {
    fun createPet(
        veterian: AuthenticatedUser,
        owner: Long,
        name: String,
        microchip: String,
        birth: OffsetDateTime?,
        breed: String?,
        imageUrl: String?
    ): Pet {
        val ownerUser = userRepository.findById(owner).orElseThrow {
            ResourceNotFoundException("User $owner Not Found")
        }

        if(animalRepository.findAnimalByChip(microchip).isNotEmpty()) {
            throw ResourceNotFoundException("Animal with chip $microchip already exists")
        }

        val pet = Pet(
            name = name,
            chip = microchip,
            breed = breed,
            birth = birth,
            imageUrl = imageUrl,
            owner = ownerUser
        )

        return petRepository.save(pet)
    }

    fun updatePet(
        veterian: AuthenticatedUser,
        petId: Long,
        name: String?,
        birth: OffsetDateTime?,
        breed: String?,
        imageUrl: String?,
        ownerId: Long?
    ): Pet {
        val pet = petRepository.findById(petId).orElseThrow {
            ResourceNotFoundException("Pet $petId Not Found")
        }

        val ownerUser = ownerId?.let {
            userRepository.findById(it).orElseThrow {
                ResourceNotFoundException("User $it Not Found")
            }
        }

        val updatedPet = pet.copy(
            name = name ?: pet.name,
            breed = breed ?: pet.breed,
            birth = birth ?: pet.birth,
            imageUrl = imageUrl ?: pet.imageUrl,
            owner = ownerUser ?: pet.owner
        )

        return petRepository.save(updatedPet)
    }

    fun assignAnimalToUser(veterinarian: AuthenticatedUser, animalId: Long, userId: Long): Pet {
        val animal = animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }

        val user = userRepository.findById(userId).orElseThrow {
            ResourceNotFoundException("User with id $userId not found")

        }

        return petRepository.save(
            Pet(
                name = animal.name,
                chip = animal.chip,
                breed = animal.breed,
                birth = animal.birth,
                imageUrl = animal.imageUrl,
                owner = user
            )
        )
    }

    fun unassignAnimalFromUser(veterinarian: AuthenticatedUser, animalId: Long): Boolean {
        animalRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Animal with id $animalId not found")
        }

        petRepository.findById(animalId).orElseThrow {
            ResourceNotFoundException("Pet with id $animalId not found")
        }

        petRepository.deleteById(animalId)

        return true
    }
}