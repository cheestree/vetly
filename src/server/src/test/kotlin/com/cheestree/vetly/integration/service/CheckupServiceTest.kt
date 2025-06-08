package com.cheestree.vetly.integration.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.http.model.input.file.StoredFileInputModel
import com.cheestree.vetly.service.CheckupService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CheckupServiceTest : IntegrationTestBase() {
    @Autowired
    private lateinit var checkupService: CheckupService

    @Nested
    inner class GetAllCheckupTests {
        @Test
        fun `should retrieve all checkups successfully`() {
            val checkups = checkupService.getAllCheckups(authenticatedUser = savedUsers[0].toAuthenticatedUser())

            assertThat(checkups.elements).hasSize(2)
        }

        @Test
        fun `should filter checkups by animal name`() {
            val checkups =
                checkupService.getAllCheckups(
                    authenticatedUser = savedUsers[0].toAuthenticatedUser(),
                    animalName = savedAnimals[0].name,
                )

            assertThat(checkups.elements).hasSize(1)
            assertThat(
                checkups.elements
                    .first()
                    .animal.name,
            ).isEqualTo("Dog")
        }

        @Test
        fun `should filter checkups by animalId`() {
            val checkups =
                checkupService.getAllCheckups(
                    authenticatedUser = savedUsers[0].toAuthenticatedUser(),
                    animalId = savedAnimals[0].id,
                )

            assertThat(checkups.elements).hasSize(1)
            assertThat(
                checkups.elements
                    .first()
                    .animal.name,
            ).isEqualTo("Dog")
        }

        @Test
        fun `should filter checkups by clinicId`() {
            val checkups =
                checkupService.getAllCheckups(
                    authenticatedUser = savedUsers[0].toAuthenticatedUser(),
                    clinicId = savedClinics[0].id,
                )

            assertThat(checkups.elements).hasSize(1)
            assertThat(
                checkups.elements
                    .first()
                    .clinic.name,
            ).isEqualTo("Happy Pets")
        }

        @Test
        fun `should filter checkups by date`() {
            val checkups =
                checkupService.getAllCheckups(
                    authenticatedUser = savedUsers[0].toAuthenticatedUser(),
                    dateTimeStart = daysAgo(1).toLocalDate(),
                )

            assertThat(checkups.elements).hasSize(2)
            assertThat(checkups.elements.first().description).isEqualTo("Routine checkup")
        }
    }

    @Nested
    inner class GetCheckupTests {
        @Test
        fun `should retrieve a checkup by ID successfully`() {
            val checkup = checkupService.getCheckup(savedUsers[0].toAuthenticatedUser(), savedCheckups[0].id)

            assertThat(checkup).isNotNull
            assertThat(checkup.id).isEqualTo(savedCheckups[0].id)
        }

        @Test
        fun `should throw NotFoundException when checkup does not exist`() {
            assertThatThrownBy {
                checkupService.getCheckup(savedUsers[0].toAuthenticatedUser(), nonExistentNumber)
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Checkup $nonExistentNumber not found"
            }
        }

        @Test
        fun `should throw NotFoundException when user does not have access to checkup`() {
            assertThatThrownBy {
                checkupService.getCheckup(savedUsers[1].toAuthenticatedUser(), savedCheckups[0].id)
            }.isInstanceOf(UnauthorizedAccessException::class.java).withFailMessage {
                "User ${savedUsers[1].id} does not have access to checkup ${savedCheckups[0].id}"
            }
        }
    }

    @Nested
    inner class CreateCheckupTests {
        @Test
        fun `should create a checkup successfully`() {
            val id = createCheckupFrom(savedCheckups[0])

            val retrievedCheckup = checkupRepository.findById(id).orElseThrow()

            assertThat(retrievedCheckup).isNotNull
            assertThat(retrievedCheckup.animal.id).isEqualTo(savedCheckups[0].animal.id)
        }

        @Test
        fun `should throw NotFoundException when creating a checkup with non-existent animal`() {
            assertThatThrownBy {
                checkupService.createCheckUp(
                    animalId = nonExistentNumber,
                    veterinarianId = savedCheckups[0].veterinarian.id,
                    clinicId = savedCheckups[0].clinic.id,
                    time = savedCheckups[0].dateTime,
                    title = savedCheckups[0].title,
                    description = savedCheckups[0].description,
                    files = listOf(),
                )
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Animal $nonExistentNumber not found"
            }
        }

        @Test
        fun `should throw NotFoundException when creating a checkup with non-existent veterinarian`() {
            assertThatThrownBy {
                checkupService.createCheckUp(
                    animalId = savedCheckups[0].animal.id,
                    veterinarianId = nonExistentNumber,
                    clinicId = savedCheckups[0].clinic.id,
                    time = savedCheckups[0].dateTime,
                    title = savedCheckups[0].title,
                    description = savedCheckups[0].description,
                    files = listOf(),
                )
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Veterinarian $nonExistentNumber not found"
            }
        }

        @Test
        fun `should throw NotFoundException when creating a checkup with non-existent clinic`() {
            assertThatThrownBy {
                checkupService.createCheckUp(
                    animalId = savedCheckups[0].animal.id,
                    veterinarianId = savedCheckups[0].veterinarian.id,
                    clinicId = nonExistentNumber,
                    time = savedCheckups[0].dateTime,
                    title = savedCheckups[0].title,
                    description = savedCheckups[0].description,
                    files = listOf(),
                )
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Clinic $nonExistentNumber not found"
            }
        }
    }

    @Nested
    inner class UpdateCheckupTests {
        @Test
        fun `should update a checkup successfully`() {
            val updatedDescription = "Updated description"
            val updatedTime = savedCheckups[0].dateTime.plusDays(1)

            val id =
                checkupService.updateCheckUp(
                    veterinarianId = savedUsers[0].id,
                    checkupId = savedCheckups[0].id,
                    description = updatedDescription,
                    dateTime = updatedTime,
                )

            val updatedCheckup = checkupRepository.findById(id).orElseThrow()

            assertThat(updatedCheckup.description).isEqualTo(updatedDescription)
            assertThat(updatedCheckup.dateTime).isEqualTo(updatedTime)
        }

        @Test
        fun `should throw NotFoundException when updating a non-existent checkup`() {
            assertThatThrownBy {
                checkupService.updateCheckUp(
                    veterinarianId = savedUsers[0].id,
                    checkupId = nonExistentNumber,
                    description = "New description",
                    dateTime = savedCheckups[0].dateTime.plusDays(1),
                )
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Checkup $nonExistentNumber not found"
            }
        }

        @Test
        fun `should throw UnauthorizedAccessException when user does not have access to update the checkup`() {
            assertThatThrownBy {
                checkupService.updateCheckUp(
                    veterinarianId = savedUsers[1].id,
                    checkupId = savedCheckups[0].id,
                    description = "New description",
                    dateTime = savedCheckups[0].dateTime.plusDays(1),
                )
            }.isInstanceOf(UnauthorizedAccessException::class.java).withFailMessage {
                "Cannot update check-up ${savedCheckups[0].id}"
            }
        }

        @Test
        fun `should throw NotFoundException when updating a checkup with non-existent veterinarian`() {
            assertThatThrownBy {
                checkupService.updateCheckUp(
                    veterinarianId = nonExistentNumber,
                    checkupId = savedCheckups[0].id,
                    description = "New description",
                    dateTime = savedCheckups[0].dateTime.plusDays(1),
                )
            }.isInstanceOf(UnauthorizedAccessException::class.java).withFailMessage {
                "Veterinarian $nonExistentNumber not found"
            }
        }
    }

    @Nested
    inner class DeleteCheckupTests {
        @Test
        fun `should delete a checkup successfully`() {
            assertThat(
                checkupService.deleteCheckup(savedUsers[0].roles.map { it.role.role }.toSet(), savedUsers[0].id, savedCheckups[0].id),
            ).isTrue()
        }

        @Test
        fun `should throw NotFoundException when deleting a non-existent checkup`() {
            assertThatThrownBy {
                checkupService.deleteCheckup(
                    savedUsers[0].roles.map { it.role.role }.toSet(),
                    savedUsers[0].id,
                    nonExistentNumber,
                )
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Checkup $nonExistentNumber not found"
            }
        }

        @Test
        fun `should throw UnauthorizedAccessException when user does not have access to delete the checkup`() {
            assertThatThrownBy {
                checkupService.deleteCheckup(
                    role = savedUsers[1].roles.map { it.role.role }.toSet(),
                    veterinarianId = savedUsers[1].id,
                    checkupId = savedCheckups[0].id,
                )
            }.isInstanceOf(UnauthorizedAccessException::class.java).withFailMessage {
                "Cannot delete check-up ${savedCheckups[0].id}"
            }
        }
    }

    private fun createCheckupFrom(checkup: Checkup): Long =
        checkupService.createCheckUp(
            animalId = checkup.animal.id,
            veterinarianId = checkup.veterinarian.id,
            clinicId = checkup.clinic.id,
            time = checkup.dateTime,
            title = savedCheckups[0].title,
            description = checkup.description,
            files = checkup.files.map { StoredFileInputModel(it.url, it.title, it.description) },
        )
}
