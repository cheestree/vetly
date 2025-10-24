package com.cheestree.vetly.checkup.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupQueryInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.service.CheckupService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile

class CheckupServiceIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var checkupService: CheckupService

    @Nested
    inner class GetAllCheckupTests {
        @Test
        fun `should retrieve all checkups successfully`() {
            val checkups = checkupService.getAllCheckups(user = savedUsers[0].toAuthenticatedUser())

            assertThat(checkups.elements).hasSize(2)
        }

        @Test
        fun `should filter checkups by animal name`() {
            val checkups =
                checkupService.getAllCheckups(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = CheckupQueryInputModel(animalName = savedAnimals[0].name),
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
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = CheckupQueryInputModel(animalId = savedAnimals[0].id),
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
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = CheckupQueryInputModel(clinicId = savedClinics[0].id),
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
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = CheckupQueryInputModel(dateTimeStart = daysAgo(1).toLocalDate()),
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
                    user = savedUsers[1].toAuthenticatedUser(),
                    createdCheckup =
                        CheckupCreateInputModel(
                            animalId = nonExistentNumber,
                            veterinarianId = savedCheckups[0].veterinarian.publicId,
                            clinicId = savedCheckups[0].clinic.id,
                            dateTime = savedCheckups[0].dateTime,
                            title = savedCheckups[0].title,
                            description = savedCheckups[0].description,
                        ),
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
                    user = savedUsers[1].toAuthenticatedUser(),
                    createdCheckup =
                        CheckupCreateInputModel(
                            animalId = savedCheckups[0].animal.id,
                            veterinarianId = nonExistentUUID,
                            clinicId = savedCheckups[0].clinic.id,
                            dateTime = savedCheckups[0].dateTime,
                            title = savedCheckups[0].title,
                            description = savedCheckups[0].description,
                        ),
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
                    user = savedUsers[1].toAuthenticatedUser(),
                    createdCheckup =
                        CheckupCreateInputModel(
                            animalId = savedCheckups[0].animal.id,
                            veterinarianId = savedCheckups[0].veterinarian.publicId,
                            clinicId = nonExistentNumber,
                            dateTime = savedCheckups[0].dateTime,
                            title = savedCheckups[0].title,
                            description = savedCheckups[0].description,
                        ),
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
            val updatedTime = savedCheckups[0].dateTime.plusDays(1)
            val wrapper =
                CheckupUpdateInputModel(
                    description = JsonNullable.of("New description"),
                    dateTime = JsonNullable.of(updatedTime),
                )

            val checkup =
                checkupService.updateCheckUp(
                    user = savedUsers[0].toAuthenticatedUser(),
                    id = savedCheckups[0].id,
                    updatedCheckup = wrapper,
                )

            val updatedCheckup = checkupRepository.findById(checkup.id).orElseThrow()

            assertThat(updatedCheckup.description).isEqualTo("New description")
            assertThat(updatedCheckup.dateTime).isEqualTo(updatedTime)
        }

        @Test
        fun `should throw NotFoundException when updating a non-existent checkup`() {
            val wrapper =
                CheckupUpdateInputModel(
                    description = JsonNullable.of("New description"),
                )

            assertThatThrownBy {
                checkupService.updateCheckUp(
                    user = savedUsers[1].toAuthenticatedUser(),
                    id = nonExistentNumber,
                    updatedCheckup = wrapper,
                )
            }.isInstanceOf(ResourceNotFoundException::class.java).withFailMessage {
                "Checkup $nonExistentNumber not found"
            }
        }

        @Test
        fun `should throw UnauthorizedAccessException when user does not have access to update the checkup`() {
            val wrapper =
                CheckupUpdateInputModel(
                    description = JsonNullable.of("New description"),
                )

            assertThatThrownBy {
                checkupService.updateCheckUp(
                    user = savedUsers[1].toAuthenticatedUser(),
                    id = savedCheckups[0].id,
                    updatedCheckup = wrapper,
                )
            }.isInstanceOf(UnauthorizedAccessException::class.java).withFailMessage {
                "Cannot update check-up ${savedCheckups[0].id}"
            }
        }
    }

    @Nested
    inner class DeleteCheckupTests {
        @Test
        fun `should delete a checkup successfully`() {
            assertThat(
                checkupService.deleteCheckup(savedUsers[0].toAuthenticatedUser(), savedCheckups[0].id),
            ).isTrue()
        }

        @Test
        fun `should throw NotFoundException when deleting a non-existent checkup`() {
            assertThatThrownBy {
                checkupService.deleteCheckup(
                    savedUsers[0].toAuthenticatedUser(),
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
                    user = savedUsers[1].toAuthenticatedUser(),
                    id = savedCheckups[0].id,
                )
            }.isInstanceOf(UnauthorizedAccessException::class.java).withFailMessage {
                "Cannot delete check-up ${savedCheckups[0].id}"
            }
        }
    }

    private fun createCheckupFrom(checkup: Checkup): Long =
        checkupService.createCheckUp(
            user = savedUsers[0].toAuthenticatedUser(),
            createdCheckup =
                CheckupCreateInputModel(
                    animalId = checkup.animal.id,
                    veterinarianId = checkup.veterinarian.publicId,
                    clinicId = checkup.clinic.id,
                    dateTime = checkup.dateTime,
                    title = savedCheckups[0].title,
                    description = checkup.description,
                ),
            files =
                checkup.files.mapIndexed { index, it ->
                    MockMultipartFile(
                        "file$index",
                        "$it.png",
                        "image/png",
                        ByteArray(10) { 0x1 },
                    )
                },
        )
}
