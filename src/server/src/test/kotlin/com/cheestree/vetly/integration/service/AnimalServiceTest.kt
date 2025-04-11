package com.cheestree.vetly.integration.service

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.service.AnimalService
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertTrue

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AnimalServiceTest {
    @Autowired
    private lateinit var animalService: AnimalService

    @Autowired
    private lateinit var animalRepository: AnimalRepository

    @Test
    fun `should retrieve all animals successfully`() {
        // Given
        val animal1 = Animal(name = "Dog", microchip = "12345", species = "Bulldog")
        val animal2 = Animal(name = "Cat", microchip = "67890", species = "Siamese")
        animalRepository.save(animal1)
        animalRepository.save(animal2)

        // When
        val animals = animalService.getAllAnimals()

        // Then
        assertThat(animals).hasSize(2)
    }

    @Test
    fun `should throw exception when animal doesn't exist on retrieve`() {
        // Given
        val animalId = 999L

        // When & Then
        assertThrows<ResourceNotFoundException> {
            animalService.getAnimal(animalId)
        }
    }

    @Test
    fun `should retrieve animal successfully`() {
        // Given
        val animal = Animal(name = "Dog", microchip = "12345", species = "Bulldog")
        val id = animalService.createAnimal(
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl,
        )

        // When
        val retrievedAnimal = animalService.getAnimal(id)

        // Then
        assertThat(retrievedAnimal.name).isEqualTo("Dog")
        assertThat(retrievedAnimal.microchip).isEqualTo("12345")
    }

    @Test
    fun `should save and retrieve animal successfully`() {
        // Given
        val animal = Animal(name = "Dog", microchip = "12345", species = "Bulldog")

        // When
        val id = animalService.createAnimal(
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl,
        )
        val retrievedAnimal = animalRepository.findById(id).orElseThrow()

        // Then
        assertThat(retrievedAnimal.name).isEqualTo("Dog")
        assertThat(retrievedAnimal.microchip).isEqualTo("12345")
    }

    @Test
    fun `should throw exception when animal with same microchip already exists`() {
        // Given
        val animal1 = Animal(name = "Dog", microchip = "12345", species = "Bulldog")
        val animal2 = Animal(name = "Cat", microchip = "12345", species = "Siamese")

        // When
        animalService.createAnimal(
            name = animal1.name,
            microchip = animal1.microchip,
            birthDate = animal1.birthDate,
            species = animal1.species,
            imageUrl = animal1.imageUrl,
        )

        // Then
        assertThrows<ResourceAlreadyExistsException> {
            animalService.createAnimal(
                name = animal2.name,
                microchip = animal2.microchip,
                birthDate = animal2.birthDate,
                species = animal2.species,
                imageUrl = animal2.imageUrl,
            )
        }
    }

    @Test
    fun `should save animal with null microchip`() {
        // Given
        val animal = Animal(name = "Dog", microchip = null, species = "Bulldog")

        // When
        val id = animalService.createAnimal(
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl,
        )
        val retrievedAnimal = animalRepository.findById(id).orElseThrow()

        // Then
        assertThat(retrievedAnimal.name).isEqualTo("Dog")
        assertThat(retrievedAnimal.microchip).isNull()
    }

    @Test
    fun `should throw exception when animal not found on update`() {
        // Given
        val animalId = 999L

        // When & Then
        assertThrows<ResourceNotFoundException> {
            animalService.updateAnimal(
                id = animalId,
                name = "Cat",
                microchip = "67890",
                birthDate = null,
                species = "Siamese",
                imageUrl = null,
            )
        }
    }

    @Test
    fun `should throw exception when animal with same microchip already exists on update`() {
        // Given
        val animal1 = Animal(name = "Dog", microchip = "12345", species = "Bulldog")
        val animal2 = Animal(name = "Cat", microchip = "67890", species = "Siamese")

        // When
        val id1 = animalService.createAnimal(
            name = animal1.name,
            microchip = animal1.microchip,
            birthDate = animal1.birthDate,
            species = animal1.species,
            imageUrl = animal1.imageUrl,
        )
        animalService.createAnimal(
            name = animal2.name,
            microchip = animal2.microchip,
            birthDate = animal2.birthDate,
            species = animal2.species,
            imageUrl = animal2.imageUrl,
        )

        // Then
        assertThrows<ResourceAlreadyExistsException> {
            animalService.updateAnimal(
                id = id1,
                name = null,
                microchip = "67890",
                birthDate = null,
                species = null,
                imageUrl = null,
            )
        }
    }
    @Test
    fun `should allow microchip update when new microchip is unique`() {
        // Given
        val originalAnimal = Animal(name = "Dog", microchip = "12345", species = "Bulldog")
        val id = animalService.createAnimal(
            name = originalAnimal.name,
            microchip = originalAnimal.microchip,
            birthDate = originalAnimal.birthDate,
            species = originalAnimal.species,
            imageUrl = originalAnimal.imageUrl,
        )

        // When
        val updatedAnimal = animalService.updateAnimal(
            id = id,
            name = null,
            microchip = "unique-chip",
            birthDate = null,
            species = null,
            imageUrl = null,
        )

        // Then
        assertThat(updatedAnimal.microchip).isEqualTo("unique-chip")
    }

    @Test
    fun `should update animal successfully`() {
        // Given
        val animal = Animal(name = "Dog", microchip = "12345", species = "Bulldog")
        val id = animalService.createAnimal(
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl,
        )

        // When
        val updatedAnimal = animalService.updateAnimal(
            id = id,
            name = "Cat",
            microchip = "67890",
            birthDate = null,
            species = "Siamese",
            imageUrl = null,
        )
        val retrievedAnimal = animalRepository.findById(id).orElseThrow()

        // Then
        assertThat(updatedAnimal.name).isEqualTo("Cat")
        assertThat(retrievedAnimal.microchip).isEqualTo("67890")
    }

    @Test
    fun `should throw exception when animal doesn't exist on delete`() {
        // Given
        val animalId = 999L

        // When & Then
        assertThrows<ResourceNotFoundException> {
            animalService.deleteAnimal(animalId)
        }
    }

    @Test
    fun `should delete animal successfully`() {
        // Given
        val animal = Animal(name = "Dog", microchip = "12345", species = "Bulldog")
        val id = animalService.createAnimal(
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl,
        )

        // When
        val retrievedAnimal = animalService.deleteAnimal(id)

        // Then
        assertTrue(retrievedAnimal)
    }
}