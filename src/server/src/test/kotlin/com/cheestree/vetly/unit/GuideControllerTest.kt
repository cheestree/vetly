package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.GuideController
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.GuideService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.BeforeTest

@WebMvcTest(GuideController::class)
class GuideControllerTest: BaseTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var guideService: GuideService

    @MockitoBean
    lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    private lateinit var guides: List<Guide>
    private lateinit var user : AuthenticatedUser

    private val invalidGuideId = "invalid"
    private val validGuideId = 1L
    private val missingGuideId = 100L

    @BeforeTest
    fun setup() {
        user = userWithAdmin.toAuthenticatedUser()

        guides = guidesBase

        guideService = mockk()
        authenticatedUserArgumentResolver = mockk()

        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc = MockMvcBuilders
            .standaloneSetup(GuideController(guideService))
            .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    private fun performGetAllGuidesRequest(params: Map<String, String> = emptyMap()): ResultActions {
        val request = get(Path.Guides.GET_ALL).apply {
            params.forEach { (key, value) -> param(key, value) }
        }

        return mockMvc.perform(request)
    }

    private fun assertGetAllSuccess(params: Map<String, String> = emptyMap(), expected: List<GuidePreview>) {
        val pageable = PageRequest.of(0, 10)
        val expectedPage = PageImpl(expected, pageable, expected.size.toLong())

        every {
            guideService.getAllGuides(
                title = any(),
                page = any(), size = any(),
                sortBy = any(), sortDirection = any()
            )
        } returns expectedPage

        performGetAllGuidesRequest(params)
            .andExpectSuccessResponse(expectedStatus = HttpStatus.OK, expectedMessage = null, expectedData = expectedPage)
    }

    @Nested
    inner class GetAllGuideTest {
        @Test
        fun `should return 200 if guides found on GET_ALL`() {
            val expected = guides.map { it.asPreview() }
            assertGetAllSuccess(expected = expected)
        }

        @Test
        fun `should return 200 if guides found with name filter`() {
            val expected = guides.filter { it.title.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
            assertGetAllSuccess(params = mapOf("title" to "Dog") , expected = expected)
        }

        @Test
        fun `should return 200 if guides found with sort direction ASC`() {
            val expected = guides.sortedBy { it.title }.map { it.asPreview() }
            assertGetAllSuccess(params = mapOf("sortBy" to "title", "sortDirection" to "ASC") , expected = expected)
        }
    }

    @Nested
    inner class GetGuideTests {
        @Test
        fun `should return 400 if guideId is invalid on GET`() {
            mockMvc.perform(
                get(Path.Guides.GET, invalidGuideId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable: guideId",
                expectedError = "Type mismatch"
            )
        }

        @Test
        fun `should return 404 if guide not found on GET`() {
            every { guideService.getGuide(
                guideId = any()
            ) } throws ResourceNotFoundException("Guide not found")

            mockMvc.perform(
                get(Path.Guides.GET, missingGuideId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Guide not found",
                expectedError = "Resource not found"
            )
        }

        @Test
        fun `should return 200 if guide found on GET`() {
            val expectedGuide = guides.first { it.id == validGuideId }.asPublic()

            every { guideService.getGuide(
                guideId = expectedGuide.id
            ) } returns expectedGuide

            mockMvc.perform(
                get(Path.Guides.GET, validGuideId)
            ).andExpectSuccessResponse<GuideInformation>(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedGuide
            )
        }
    }

    @Nested
    inner class CreateGuideTests {
        @Test
        fun `should return 409 if guide title exists with the same veterinarian on CREATE`(){
            val expectedGuide = guides.first { it.id == validGuideId }
            val createdGuide = GuideCreateInputModel(
                title = expectedGuide.title,
                description = expectedGuide.description,
                imageUrl = expectedGuide.imageUrl,
                content = expectedGuide.content
            )

            every { guideService.createGuide(
                veterinarianId = any(),
                title = any(),
                description = any(),
                imageUrl = any(),
                content = any()
            ) } throws ResourceAlreadyExistsException("Guide with title ${expectedGuide.title} already exists for user ${expectedGuide.user.id}")

            mockMvc.perform(
                post(Path.Guides.CREATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createdGuide.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.CONFLICT,
                expectedMessage = "Resource already exists: Guide with title ${expectedGuide.title} already exists for user ${expectedGuide.user.id}",
                expectedError = "Resource already exists"
            )
        }

        @Test
        fun `should return 200 if guide created successfully`() {
            val expectedGuide = guides.first()
            val createdGuide = GuideCreateInputModel(
                title = expectedGuide.title,
                description = expectedGuide.description,
                imageUrl = expectedGuide.imageUrl,
                content = expectedGuide.content
            )

            every { guideService.createGuide(
                veterinarianId = any(),
                title = any(),
                description = any(),
                imageUrl = any(),
                content = any()
            ) } returns expectedGuide.id

            mockMvc.perform(
                post(Path.Guides.CREATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createdGuide.toJson())
            ).andExpectSuccessResponse<Map<String, Long>>(
                expectedStatus = HttpStatus.CREATED,
                expectedMessage = null,
                expectedData = mapOf("id" to expectedGuide.id)
            )
        }
    }

    @Nested
    inner class UpdateGuideTests {
        @Test
        fun `should return 400 if guideId is invalid on UPDATE`() {
            val updatedGuide = GuideUpdateInputModel(
                title = "Dog Care v2",
                description = "Dog Care v2",
                imageUrl = "https://example.com/dog-care-v2.jpg",
                content = "Dog Care v2 content",
            )

            mockMvc.perform(
                put(Path.Guides.UPDATE, invalidGuideId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedGuide.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable: guideId",
                expectedError = "Type mismatch"
            )
        }

        @Test
        fun `should return 404 if guide not found on UPDATE`() {
            val updatedGuide = GuideUpdateInputModel(
                title = "Dog Care v2",
                description = "Dog Care v2",
                imageUrl = "https://example.com/dog-care-v2.jpg",
                content = "Dog Care v2 content",
            )

            every { guideService.updateGuide(
                veterinarianId = any(),
                roles = any(),
                guideId = validGuideId,
                title = updatedGuide.title,
                description = updatedGuide.description,
                imageUrl = updatedGuide.imageUrl,
                content = updatedGuide.content
            ) } throws ResourceNotFoundException("Guide not found")

            mockMvc.perform(
                put(Path.Guides.UPDATE, validGuideId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedGuide.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Guide not found",
                expectedError = "Resource not found"
            )
        }

        @Test
        fun `should return 200 if guide updated successfully`() {
            val expectedGuide = guides.first()
            val updatedGuide = GuideUpdateInputModel(
                title = "Dog Care v2",
                description = "Dog Care v2",
                imageUrl = "https://example.com/dog-care-v2.jpg",
                content = "Dog Care v2 content",
            )

            every { guideService.updateGuide(
                veterinarianId = any(),
                roles = any(),
                guideId = expectedGuide.id,
                title = updatedGuide.title,
                description = updatedGuide.description,
                imageUrl = updatedGuide.imageUrl,
                content = updatedGuide.content
            ) } returns expectedGuide.asPublic()

            mockMvc.perform(
                put(Path.Guides.UPDATE, expectedGuide.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedGuide.toJson())
            ).andExpectSuccessResponse<Void>(
                expectedStatus = HttpStatus.NO_CONTENT,
                expectedMessage = null,
                expectedData = null
            )
        }
    }

    @Nested
    inner class DeleteGuideTest {
        @Test
        fun `should return 400 if guideId is invalid on DELETE`() {
            mockMvc.perform(
                delete(Path.Guides.DELETE, invalidGuideId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable: guideId",
                expectedError = "Type mismatch"
            )
        }

        @Test
        fun `should return 404 if guide not found on DELETE`() {
            val guideId = 5L
            every { guideService.deleteGuide(
                veterinarianId = any(),
                roles = any(),
                guideId = guideId
            ) } throws ResourceNotFoundException("Guide not found")

            mockMvc.perform(
                delete(Path.Guides.DELETE, guideId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Guide not found",
                expectedError = "Resource not found"
            )
        }

        @Test
        fun `should return 204 if guide deleted successfully`() {
            val guideId = 1L
            every { guideService.deleteGuide(
                veterinarianId = any(),
                roles = any(),
                guideId = guideId
            ) } returns true

            mockMvc.perform(
                delete(Path.Guides.DELETE, guideId)
            ).andExpectSuccessResponse<Void>(
                expectedStatus = HttpStatus.NO_CONTENT,
                expectedMessage = null,
                expectedData = null
            )
        }
    }
}