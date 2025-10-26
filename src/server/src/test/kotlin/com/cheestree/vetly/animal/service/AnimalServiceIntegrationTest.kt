package com.cheestree.vetly.animal.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.exception.VetException.*
import com.cheestree.vetly.http.model.input.animal.AnimalCreateInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalQueryInputModel
import com.cheestree.vetly.http.model.input.animal.AnimalUpdateInputModel
import com.cheestree.vetly.http.model.output.animal.AnimalInformation
import com.cheestree.vetly.service.AnimalService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.beans.factory.annotation.Autowired

class AnimalServiceIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var animalService: AnimalService

    @Nested
    inner class GetAllAnimalTests {
        @Test
        fun `should retrieve all animals successfully`() {
            val animals = animalService.getAllAnimals(user = savedUsers[0].toAuthenticatedUser())

            assertThat(animals.elements).hasSize(savedAnimals.size)
        }

        @Test
        fun `should filter animals by name`() {
            val animals =
                animalService.getAllAnimals(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = AnimalQueryInputModel(name = savedAnimals[0].name),
                )
            println(animals.elements)

            assertThat(animals.elements).hasSize(1)
            assertThat(animals.elements[0].name).isEqualTo("Dog")
        }

        @Test
        fun `should filter animals by microchip`() {
            val animals =
                animalService.getAllAnimals(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = AnimalQueryInputModel(microchip = savedAnimals[0].microchip),
                )

            assertThat(animals.elements).hasSize(1)
            assertThat(animals.elements[0].name).isEqualTo("Dog")
        }

        @Test
        fun `should filter animals by species`() {
            val animals =
                animalService.getAllAnimals(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = AnimalQueryInputModel(species = savedAnimals[2].species),
                )

            assertThat(animals.elements).hasSize(1)
            assertThat(animals.elements[0].species).isEqualTo("Macaw")
        }

        @Test
        fun `should filter animals by birth date`() {
            val animals =
                animalService.getAllAnimals(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = AnimalQueryInputModel(
                        startBirthdate = savedAnimals[1].birthDate?.toLocalDate(),
                        endBirthdate = savedAnimals[1].birthDate?.toLocalDate()
                    ),
                )

            assertThat(animals.elements).hasSize(1)
            assertThat(animals.elements[0].name).isEqualTo("Cat")
        }

        @Test
        fun `should filter animals by user ID`() {
            val result =
                animalService.getAllAnimals(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = AnimalQueryInputModel(userEmail = savedUsers[0].email, owned = true),
                )

            assertThat(result.elements).hasSize(1)
            assertThat(result.elements[0].name).isEqualTo("Rabbit")
        }

        @Test
        fun `should filter owned and unowned animals`() {
            val owned =
                animalService.getAllAnimals(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = AnimalQueryInputModel(owned = true),
                )
            assertThat(owned.elements).hasSize(1)
            assertThat(owned.elements[0].name).isEqualTo("Rabbit")

            val unowned =
                animalService.getAllAnimals(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = AnimalQueryInputModel(owned = false),
                )
            assertThat(unowned.elements).hasSize(3)
            assertThat(unowned.elements[0].name).isEqualTo("Parrot")
        }
    }

    @Nested
    inner class GetAnimalTests {
        @Test
        fun `should retrieve animal successfully`() {
            val retrievedAnimal = animalService.getAnimal(savedAnimals[0].id)

            assertThat(retrievedAnimal.name).isEqualTo("Dog")
            assertThat(retrievedAnimal.microchip).isEqualTo("1234567890")
        }

        @Test
        fun `should throw exception when animal doesn't exist on retrieve`() {
            assertThatThrownBy { animalService.getAnimal(nonExistentNumber) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .withFailMessage {
                    "Animal $nonExistentNumber not found"
                }
        }
    }

    @Nested
    inner class CreateAnimalTests {
        @Test
        fun `should save and retrieve animal successfully`() {
            val animal =
                createAnimalFrom(
                    Animal(
                        name = "Dog",
                        microchip = "999999999",
                        species = "Bulldog",
                        birthDate = daysAgo(1),
                        owner = null,
                    ),
                )

            val retrievedAnimal = animalRepository.findById(animal.id).orElseThrow()

            assertThat(retrievedAnimal.name).isEqualTo("Dog")
            assertThat(retrievedAnimal.microchip).isEqualTo("999999999")

            retrievedAnimal.owner?.let {
                assertThat(it.animals).isEmpty()
            }
        }

        @Test
        fun `should throw exception when animal with same microchip already exists`() {
            assertThatThrownBy { createAnimalFrom(savedAnimals[0]) }
                .isInstanceOf(ResourceAlreadyExistsException::class.java)
                .withFailMessage {
                    "Animal with microchip ${savedAnimals[0].microchip} already exists"
                }
        }

        @Test
        fun `should save animal with null microchip`() {
            val animal = createAnimalFrom(savedAnimals[1])

            val retrievedAnimal = animalRepository.findById(animal.id).orElseThrow()

            assertThat(retrievedAnimal.name).isEqualTo(savedAnimals[1].name)
            assertThat(retrievedAnimal.microchip).isNull()

            retrievedAnimal.owner?.let {
                assertThat(it.animals).hasSize(1)
            }
        }
    }

    @Nested
    inner class UpdateAnimalTests {
        @Test
        fun `should throw exception when animal not found on update`() {
            val animalUpdate = AnimalUpdateInputModel()

            assertThatThrownBy {
                animalService.updateAnimal(
                    id = nonExistentNumber,
                    updatedAnimal = animalUpdate,
                    image = null
                )
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Animal $nonExistentNumber not found"
            }
        }

        @Test
        fun `should throw exception when animal with same microchip already exists on update`() {
            val animal = savedAnimals.first { it.microchip != null }
            val animalUpdate =
                AnimalUpdateInputModel(
                    microchip = JsonNullable.of(animal.microchip),
                )

            assertThatThrownBy {
                animalService.updateAnimal(
                    id = savedAnimals[1].id,
                    updatedAnimal = animalUpdate,
                    image = null
                )
            }.isInstanceOf(ResourceAlreadyExistsException::class.java).withFailMessage {
                "Animal with microchip ${animal.microchip} already exists"
            }
        }

        @Test
        fun `should throw exception when animal is not active on update`() {
            animalService.deleteAnimal(savedAnimals[0].id)

            val animalUpdate =
                AnimalUpdateInputModel(
                    microchip = JsonNullable.of("12345"),
                )

            assertThatThrownBy {
                animalService.updateAnimal(
                    id = savedAnimals[0].id,
                    updatedAnimal = animalUpdate,
                    image = null
                )
            }.isInstanceOf(InactiveResourceException::class.java).withFailMessage {
                "Animal with id ${savedAnimals[0].id} is not active"
            }
        }

        @Test
        fun `should update microchip when new value is null`() {
            val animalUpdate =
                AnimalUpdateInputModel(
                    microchip = JsonNullable.of(null),
                )

            val updatedAnimal =
                animalService.updateAnimal(
                    id = savedAnimals[0].id,
                    updatedAnimal = animalUpdate,
                    image = null
                )

            assertThat(updatedAnimal.microchip).isNull()
        }

        @Test
        fun `should allow microchip update when new microchip is unique`() {
            val animalUpdate =
                AnimalUpdateInputModel(
                    microchip = JsonNullable.of("unique-chip"),
                )

            val updatedAnimal =
                animalService.updateAnimal(
                    id = savedAnimals[0].id,
                    updatedAnimal = animalUpdate,
                    image = null
                )

            assertThat(updatedAnimal.microchip).isEqualTo("unique-chip")

            updatedAnimal.owner?.let {
                userRepository.findByPublicId(it.id).ifPresent { user ->
                    assertThat(user.animals).hasSize(1)
                }
            }
        }

        @Test
        fun `should update animal successfully`() {
            val animalUpdate =
                AnimalUpdateInputModel(
                    name = JsonNullable.of("Got that dog in me"),
                    microchip = JsonNullable.of("242424242422"),
                )

            val updatedAnimal =
                animalService.updateAnimal(
                    id = savedAnimals[0].id,
                    updatedAnimal = animalUpdate,
                    image = null
                )
            val retrievedAnimal = animalRepository.findById(savedAnimals[0].id).orElseThrow()

            assertThat(updatedAnimal.name).isEqualTo("Got that dog in me")
            assertThat(retrievedAnimal.microchip).isEqualTo("242424242422")
        }
    }

    @Nested
    inner class DeleteAnimalTests {
        @Test
        fun `should throw exception when animal doesn't exist on delete`() {
            assertThatThrownBy { animalService.deleteAnimal(nonExistentNumber) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .withFailMessage {
                    "Animal $nonExistentNumber not found"
                }
        }

        @Test
        fun `should delete animal successfully`() {
            val ownerBeforeDelete = savedAnimals[0].owner
            val ownerAnimalsBeforeDelete = ownerBeforeDelete?.animals?.size ?: 0

            assertThat(animalService.deleteAnimal(savedAnimals[0].id)).isTrue()

            savedAnimals[0].owner?.let {
                userRepository.findById(it.id).ifPresent { user ->
                    assertThat(user.animals).hasSize(ownerAnimalsBeforeDelete - 1)
                }
            }
        }
    }

    private fun createAnimalFrom(animal: Animal): AnimalInformation =
        animalService.createAnimal(
            createdAnimal =
                AnimalCreateInputModel(
                    name = animal.name,
                    microchip = animal.microchip,
                    species = animal.species,
                    sterilized = animal.sterilized,
                    sex = animal.sex,
                    birthDate = animal.birthDate,
                ),
            image = null,
        )
}
