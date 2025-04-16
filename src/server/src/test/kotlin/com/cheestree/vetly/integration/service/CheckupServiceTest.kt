package com.cheestree.vetly.integration.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.service.CheckupService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class CheckupServiceTest : IntegrationTestBase() {

    @Autowired
    private lateinit var checkupService: CheckupService

    @Nested
    inner class GetAllCheckupTests {

        @Test
        fun `should retrieve all checkups successfully`() {
            val checkups = checkupService.getAllCheckups()

            assertThat(checkups).hasSize(2)
        }

        @Test
        fun `should filter checkups by animal name`() {
            val checkups = checkupService.getAllCheckups(animalName = "Dog")

            assertThat(checkups).hasSize(1)
            assertThat(checkups.first().animal.name).isEqualTo("Dog")
        }

        @Test
        fun `should filter checkups by animalId`() {
            val dogId = savedAnimals.first { it.name == "Dog" }.id

            val checkups = checkupService.getAllCheckups(animalId = dogId)

            assertThat(checkups).hasSize(1)
            assertThat(checkups.first().animal.name).isEqualTo("Dog")
        }

        @Test
        fun `should filter checkups by clinicId`() {
            val clinicId = savedClinics.first { it.name == "Happy Pets" }.id

            val checkups = checkupService.getAllCheckups(clinicId = clinicId)

            assertThat(checkups).hasSize(1)
            assertThat(checkups.first().clinic.name).isEqualTo("Happy Pets")
        }

        @Test
        fun `should filter checkups by date`() {
            val date = daysAgo(1)

            val checkups = checkupService.getAllCheckups(dateTimeStart = date)

            assertThat(checkups).hasSize(1)
            assertThat(checkups.first().description).isEqualTo("Routine checkup")
        }
    }

    @Nested
    inner class GetCheckupTests {

    }

    @Nested
    inner class CreateCheckupTests {

    }

    @Nested
    inner class UpdateCheckupTests {

    }

    @Nested
    inner class DeleteCheckupTests {

    }
}