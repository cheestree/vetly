package com.cheestree.vetly.integration.service

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.repository.AnimalRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.AnimalService
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertTrue
import java.time.OffsetDateTime

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AnimalServiceTest : BaseTest() {
    @Autowired
    private lateinit var animalService: AnimalService

    @Autowired
    private lateinit var animalRepository: AnimalRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should retrieve all animals successfully`() {
        // Given
        animalRepository.save(DEFAULT_DOG)
        animalRepository.save(DEFAULT_CAT)

        // When
        val animals = animalService.getAllAnimals()

        // Then
        assertThat(animals).hasSize(2)
    }

    @Test
    fun `should filter animals by name`() {
        animalRepository.save(Animal(name = "Rex", imageUrl = "rex.jpg"))
        animalRepository.save(Animal(name = "Milo", imageUrl = "milo.jpg"))

        val animals = animalService.getAllAnimals(name = "rex")

        assertThat(animals).hasSize(1)
        assertThat(animals.content[0].name).isEqualTo("Rex")
    }

    @Test
    fun `should filter animals by microchip`() {
        animalRepository.save(Animal(name = "Ghost", microchip = "XYZ-123", imageUrl = "ghost.png"))
        animalRepository.save(Animal(name = "Spot", microchip = "ABC-999", imageUrl = "spot.png"))

        val animals = animalService.getAllAnimals(microchip = "XYZ-123")

        assertThat(animals).hasSize(1)
        assertThat(animals.content[0].name).isEqualTo("Ghost")
    }

    @Test
    fun `should filter animals by species`() {
        animalRepository.save(Animal(name = "Garfield", species = "Cat", imageUrl = "garfield.png"))
        animalRepository.save(Animal(name = "Snoopy", species = "Dog", imageUrl = "snoopy.png"))

        val animals = animalService.getAllAnimals(species = "cat")

        assertThat(animals).hasSize(1)
        assertThat(animals.content[0].name).isEqualTo("Garfield")
    }

    @Test
    fun `should filter animals by birth date`() {
        val targetDate = OffsetDateTime.parse("2020-01-01T00:00:00Z")

        animalRepository.save(Animal(name = "Whiskers", birthDate = targetDate, imageUrl = "whiskers.jpg"))
        animalRepository.save(Animal(name = "Toto", birthDate = daysAgo(), imageUrl = "toto.jpg"))

        val animals = animalService.getAllAnimals(birthDate = targetDate)

        assertThat(animals).hasSize(1)
        assertThat(animals.content[0].name).isEqualTo("Whiskers")
    }

    @Test
    fun `should filter animals by user ID`() {
        val user1 = userRepository.save(buildTestUser(username = "Owner1", email = "owner1@example.com"))
        val user2 = userRepository.save(buildTestUser(username = "Owner2", email = "owner2@example.com"))

        animalRepository.save(Animal(name = "Lassie", owner = user1, imageUrl = "lassie.png"))
        animalRepository.save(Animal(name = "Pluto", owner = user2, imageUrl = "pluto.png"))

        val result = animalService.getAllAnimals(userId = user1.id)

        assertThat(result).hasSize(1)
        assertThat(result.content[0].name).isEqualTo("Lassie")
    }

    @Test
    fun `should filter owned and unowned animals`() {
        val user = userRepository.save(buildTestUser(username = "Ana", email = "ana@example.com"))

        animalRepository.save(Animal(name = "OwnedCat", owner = user, imageUrl = "cat.png"))
        animalRepository.save(Animal(name = "StrayDog", owner = null, imageUrl = "dog.png"))

        val owned = animalService.getAllAnimals(owned = true)
        assertThat(owned).hasSize(1)
        assertThat(owned.content[0].name).isEqualTo("OwnedCat")

        val unowned = animalService.getAllAnimals(owned = false)
        assertThat(unowned).hasSize(1)
        assertThat(unowned.content[0].name).isEqualTo("StrayDog")
    }

    @Test
    fun `should throw exception when animal doesn't exist on retrieve`() {
        // When & Then
        assertThrows<ResourceNotFoundException> {
            animalService.getAnimal(MISSING_ANIMAL_ID)
        }
    }

    @Test
    fun `should retrieve animal successfully`() {
        // Given
        val id = createAnimalFrom(DEFAULT_DOG)

        // When
        val retrievedAnimal = animalService.getAnimal(id)

        // Then
        assertThat(retrievedAnimal.name).isEqualTo(DEFAULT_DOG.name)
        assertThat(retrievedAnimal.microchip).isEqualTo(DEFAULT_DOG.microchip)
    }

    @Test
    fun `should save and retrieve animal successfully`() {
        // Given
        val id = createAnimalFrom(DEFAULT_DOG)

        // When
        val retrievedAnimal = animalRepository.findById(id).orElseThrow()

        // Then
        assertThat(retrievedAnimal.name).isEqualTo(DEFAULT_DOG.name)
        assertThat(retrievedAnimal.microchip).isEqualTo(DEFAULT_DOG.microchip)
    }

    @Test
    fun `should throw exception when animal with same microchip already exists`() {
        // Given
        createAnimalFrom(DEFAULT_DOG)

        // Then
        assertThrows<ResourceAlreadyExistsException> {
            createAnimalFrom(DUPLICATE_MICROCHIP_CAT)
        }
    }

    @Test
    fun `should save animal with null microchip`() {
        // Given
        val id = createAnimalFrom(NULL_MICROCHIP_DOG)

        // When
        val retrievedAnimal = animalRepository.findById(id).orElseThrow()

        // Then
        assertThat(retrievedAnimal.name).isEqualTo(NULL_MICROCHIP_DOG.name)
        assertThat(retrievedAnimal.microchip).isNull()
    }

    @Test
    fun `should throw exception when animal not found on update`() {
        // When & Then
        assertThrows<ResourceNotFoundException> {
            animalService.updateAnimal(
                id = MISSING_ANIMAL_ID,
                name = UPDATED_NAME,
                microchip = UPDATED_MICROCHIP,
                birthDate = null,
                species = UPDATED_SPECIES,
                imageUrl = null,
            )
        }
    }

    @Test
    fun `should throw exception when animal with same microchip already exists on update`() {
        // Given
        val id1 = createAnimalFrom(DEFAULT_DOG)
        createAnimalFrom(DEFAULT_CAT)

        // Then
        assertThrows<ResourceAlreadyExistsException> {
            animalService.updateAnimal(
                id = id1,
                name = null,
                microchip = UPDATED_CAT.microchip,
                birthDate = null,
                species = null,
                imageUrl = null,
            )
        }
    }

    @Test
    fun `should not update microchip when new value is null`() {
        // Given
        val id = createAnimalFrom(DEFAULT_DOG)

        // When
        val updatedAnimal = animalService.updateAnimal(
            id = id,
            name = null,
            microchip = null,
            birthDate = null,
            species = null,
            imageUrl = null,
        )
        val retrievedAnimal = animalRepository.findById(id).orElseThrow()

        // Then
        assertThat(updatedAnimal.microchip).isEqualTo(DEFAULT_MICROCHIP)
        assertThat(retrievedAnimal.microchip).isEqualTo(DEFAULT_MICROCHIP)
    }

    @Test
    fun `should allow microchip update when new microchip is unique`() {
        // Given
        val id = createAnimalFrom(DEFAULT_DOG)

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
        val id = createAnimalFrom(DEFAULT_DOG)

        // When
        val updatedAnimal = animalService.updateAnimal(
            id = id,
            name = UPDATED_NAME,
            microchip = UPDATED_MICROCHIP,
            birthDate = null,
            species = UPDATED_SPECIES,
            imageUrl = null,
        )
        val retrievedAnimal = animalRepository.findById(id).orElseThrow()

        // Then
        assertThat(updatedAnimal.name).isEqualTo(UPDATED_NAME)
        assertThat(retrievedAnimal.microchip).isEqualTo(UPDATED_MICROCHIP)
    }

    @Test
    fun `should throw exception when animal doesn't exist on delete`() {
        // When & Then
        assertThrows<ResourceNotFoundException> {
            animalService.deleteAnimal(MISSING_ANIMAL_ID)
        }
    }

    @Test
    fun `should delete animal successfully`() {
        // Given
        val id = createAnimalFrom(DEFAULT_DOG)

        // When
        val deleted = animalService.deleteAnimal(id)

        // Then
        assertTrue(deleted)
    }

    fun buildTestUser(
        username: String = "testuser",
        email: String = "test@example.com",
        uid: String? = null,
        imageUrl: String? = null,
        phone: Int? = null,
        birthDate: OffsetDateTime? = null,
        roles: Set<UserRole> = setOf(),
    ): User {
        return User(
            username = username,
            email = email,
            uid = uid,
            imageUrl = imageUrl,
            phone = phone,
            birthDate = birthDate,
            roles = roles
        )
    }

    private fun createAnimalFrom(animal: Animal): Long {
        return animalService.createAnimal(
            name = animal.name,
            microchip = animal.microchip,
            birthDate = animal.birthDate,
            species = animal.species,
            imageUrl = animal.imageUrl
        )
    }

    companion object {
        private const val MISSING_ANIMAL_ID = 999L

        private const val DEFAULT_NAME = "Dog"
        private const val DEFAULT_MICROCHIP = "12345"
        private const val DEFAULT_SPECIES = "Bulldog"

        private const val UPDATED_NAME = "Cat"
        private const val UPDATED_MICROCHIP = "67890"
        private const val UPDATED_SPECIES = "Siamese"

        val DEFAULT_DOG = Animal(name = DEFAULT_NAME, microchip = DEFAULT_MICROCHIP, species = DEFAULT_SPECIES)
        val DEFAULT_CAT = Animal(name = UPDATED_NAME, microchip = UPDATED_MICROCHIP, species = UPDATED_SPECIES)
        val UPDATED_CAT = Animal(name = UPDATED_NAME, microchip = UPDATED_MICROCHIP, species = UPDATED_SPECIES)
        val DUPLICATE_MICROCHIP_CAT = Animal(name = "Cat", microchip = DEFAULT_MICROCHIP, species = "Siamese")
        val NULL_MICROCHIP_DOG = Animal(name = DEFAULT_NAME, microchip = null, species = DEFAULT_SPECIES)
    }
}