package com.cheestree.vetly.integration.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.service.GuideService
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
class GuideServiceTest : IntegrationTestBase() {

    @Autowired
    private lateinit var guideService: GuideService

    @Nested
    inner class GetAllGuideTests {
        @Test
        fun `should retrieve all guides successfully`() {
            val guides = guideService.getAllGuides()

            assertThat(guides).hasSize(savedGuides.size)
        }
        @Test
        fun `should filter guides by title`() {
            val guides = guideService.getAllGuides(title = "Dog")

            assertThat(guides).hasSize(1)
            assertThat(guides.content[0].title).isEqualTo("Dog Care")
        }
    }

    @Nested
    inner class GetGuideTests {
        @Test
        fun `should retrieve a guide by ID successfully`() {
            val guide = guideService.getGuide(savedGuides[0].id)

            assertThat(guide).isNotNull
            assertThat(guide.id).isEqualTo(savedGuides[0].id)
        }

        @Test
        fun `should throw an exception when guide not found`() {
            assertThatThrownBy { guideService.getGuide(nonExistentNumber) }
                .isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Guide with id $nonExistentNumber not found")
        }
    }

    @Nested
    inner class CreateGuideTests {
        @Test
        fun `should create a guide successfully`() {
            val newGuideId = guideService.createGuide(
                veterinarianId = savedUsers[1].id,
                title = "New Guide",
                description = "Guide Description",
                imageUrl = null,
                content = "Guide Content"
            )

            val createdGuide = guideService.getGuide(newGuideId)

            assertThat(createdGuide).isNotNull
            assertThat(createdGuide.id).isEqualTo(newGuideId)
            assertThat(createdGuide.title).isEqualTo("New Guide")
        }

        @Test
        fun `should throw an exception when veterinarian not found`() {
            assertThatThrownBy {
                guideService.createGuide(
                    veterinarianId = nonExistentNumber,
                    title = "New Guide",
                    description = "Guide Description",
                    imageUrl = null,
                    content = "Guide Content"
                )
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Veterinarian with id $nonExistentNumber not found")
        }
    }

    @Nested
    inner class UpdateGuideTests {
        @Test
        fun `should update a guide successfully`() {
            val updatedGuide = guideService.updateGuide(
                veterinarianId = savedUsers[0].id,
                roles = savedUsers[0].roles.map { it.role.role }.toSet(),
                guideId = savedGuides[0].id,
                title = "Updated Guide",
                description = "Updated Description",
                imageUrl = null,
                content = "Updated Content"
            )

            assertThat(updatedGuide).isNotNull
            assertThat(updatedGuide.title).isEqualTo("Updated Guide")
        }

        @Test
        fun `should throw an exception when guide not found`() {
            assertThatThrownBy {
                guideService.updateGuide(
                    veterinarianId = savedUsers[0].id,
                    roles = savedUsers[0].roles.map { it.role.role }.toSet(),
                    guideId = nonExistentNumber,
                    title = "Updated Guide",
                    description = "Updated Description",
                    imageUrl = null,
                    content = "Updated Content"
                )
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Guide with id $nonExistentNumber not found")
        }
    }

    @Nested
    inner class DeleteGuideTests {
        @Test
        fun `should delete a guide successfully`() {
            assertThat(
                guideService.deleteGuide(
                    veterinarianId = savedUsers[0].id,
                    roles = savedUsers[0].roles.map { it.role.role }.toSet(),
                    guideId = savedGuides[0].id
                )
            ).isTrue()
        }

        @Test
        fun `should throw an exception when guide not found for deletion`() {
            assertThatThrownBy {
                guideService.deleteGuide(
                    veterinarianId = savedUsers[0].id,
                    roles = savedUsers[0].roles.map { it.role.role }.toSet(),
                    guideId = nonExistentNumber
                )
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessage("Guide with id $nonExistentNumber not found")
        }
    }
}