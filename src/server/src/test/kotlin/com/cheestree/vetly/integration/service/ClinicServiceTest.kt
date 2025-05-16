package com.cheestree.vetly.integration.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.service.ClinicService
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ClinicServiceTest : IntegrationTestBase() {
    @Autowired
    private lateinit var clinicService: ClinicService

    @Nested
    inner class GetAllClinicTests {
        @Test
        fun `should retrieve all clinics successfully`() {
            val clinics = clinicService.getAllClinics()

            assertThat(clinics.elements).hasSize(savedClinics.size)
        }

        @Test
        fun `should filter clinics by name`() {
            val clinics = clinicService.getAllClinics(name = "Happy Pets")

            assertThat(clinics.elements).hasSize(1)
            assertThat(clinics.elements[0].name).isEqualTo("Happy Pets")
        }

        @Test
        fun `should filter clinics by latitude and longitude`() {
            val clinics = clinicService.getAllClinics(lat = 1.0, lng = 1.0)

            assertThat(clinics.elements).hasSize(1)
            assertThat(clinics.elements[0].name).isEqualTo("Happy Pets")
        }
    }

    @Nested
    inner class GetClinicTests {
        @Test
        fun `should retrieve clinic successfully`() {
            val retrievedAnimal = clinicService.getClinic(savedClinics[0].id)

            assertThat(retrievedAnimal.name).isEqualTo("Happy Pets")
            assertThat(retrievedAnimal.address).isEqualTo("123 Pet Street")
        }

        @Test
        fun `should throw exception when clinic not found`() {
            assertThatThrownBy { clinicService.getClinic(nonExistentNumber) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Clinic with id $nonExistentNumber not found")
        }
    }

    @Nested
    inner class CreateClinicTests {
        @Test
        fun `should create clinic successfully`() {
            val clinicId =
                clinicService.createClinic(
                    name = "New Clinic",
                    nif = "113456789",
                    address = "Test Address",
                    lng = 1.0,
                    lat = 1.0,
                    phone = "123456789",
                    email = "test@test.com",
                    imageUrl = null,
                    ownerId = null,
                )

            val clinic = clinicService.getClinic(clinicId)
            assertThat(clinic.name).isEqualTo("New Clinic")
            assertThat(clinic.address).isEqualTo("Test Address")
        }

        @Test
        fun `should throw exception when creating clinic with duplicate NIF`() {
            assertThatThrownBy {
                clinicService.createClinic(
                    name = "New Clinic",
                    nif = savedClinics[0].nif,
                    address = "Test Address",
                    lng = 1.0,
                    lat = 1.0,
                    phone = "123456789",
                    email = "test@test.com",
                    imageUrl = null,
                    ownerId = null,
                )
            }
                .isInstanceOf(ResourceAlreadyExistsException::class.java)
                .hasMessage("Clinic with NIF ${savedClinics[0].nif} already exists")
        }

        @Test
        fun `should throw exception when creating clinic with non-existent owner`() {
            assertThatThrownBy {
                clinicService.createClinic(
                    name = "New Clinic",
                    nif = "123456789",
                    address = "Test Address",
                    lng = 1.0,
                    lat = 1.0,
                    phone = "123456789",
                    email = "test@test.com",
                    imageUrl = null,
                    ownerId = nonExistentNumber,
                )
            }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("User with id $nonExistentNumber not found")
        }
    }

    @Nested
    inner class UpdateClinicTests {
        @Test
        fun `should update clinic successfully`() {
            val clinicId = savedClinics[0].id
            val updatedClinicId =
                clinicService.updateClinic(
                    clinicId = clinicId,
                    name = "Updated Clinic",
                    address = "Updated Address",
                )

            val clinic = clinicService.getClinic(updatedClinicId)
            assertThat(clinic.name).isEqualTo("Updated Clinic")
            assertThat(clinic.address).isEqualTo("Updated Address")
        }

        @Test
        fun `should throw exception when updating non-existent clinic`() {
            assertThatThrownBy {
                clinicService.updateClinic(
                    clinicId = nonExistentNumber,
                    name = "Updated Clinic",
                )
            }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Clinic with id $nonExistentNumber not found")
        }

        @Test
        fun `should throw exception when updating clinic with non-existent owner`() {
            assertThatThrownBy {
                clinicService.updateClinic(
                    clinicId = savedClinics[0].id,
                    ownerId = nonExistentNumber,
                )
            }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("User with id $nonExistentNumber not found")
        }

        @Test
        fun `should throw exception when adding non-veterinarian user to clinic`() {
            val clinicId = savedClinics[0].id
            val userId = savedUsers[2].id

            assertThatThrownBy { clinicService.addClinicMember(clinicId, userId) }
                .isInstanceOf(ForbiddenException::class.java)
                .hasMessage("User $userId is not a veterinarian")
        }

        @Test
        fun `should add clinic member successfully`() {
            val clinicId = savedClinics[0].id
            val userId = savedUsers[1].id

            clinicService.addClinicMember(clinicId, userId)

            val clinic =
                clinicRepository.findById(clinicId).orElseThrow {
                    ResourceNotFoundException(ResourceType.CLINIC, clinicId)
                }
            assertThat(clinic.clinicMemberships).hasSize(1)
            assertThat(clinic.clinicMemberships.first().veterinarian.id).isEqualTo(userId)
        }
    }

    @Nested
    inner class DeleteClinicTests {
        @Test
        fun `should delete clinic successfully`() {
            val clinicId = savedClinics[0].id

            clinicService.deleteClinic(clinicId)

            assertThatThrownBy { clinicService.getClinic(clinicId) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Clinic with id $clinicId not found")
        }

        @Test
        fun `should throw exception when deleting non-existent clinic`() {
            assertThatThrownBy { clinicService.deleteClinic(nonExistentNumber) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Clinic with id $nonExistentNumber not found")
        }
    }
}
