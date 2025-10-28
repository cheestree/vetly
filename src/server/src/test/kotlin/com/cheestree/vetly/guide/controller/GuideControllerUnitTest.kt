package com.cheestree.vetly.guide.controller

import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.config.JacksonConfig
import com.cheestree.vetly.controller.GuideController
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.http.GlobalExceptionHandler
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.http.resolver.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.service.GuideService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class GuideControllerUnitTest : UnitTestBase() {
    @MockitoBean
    lateinit var userService: UserService

    private val objectMapper = JacksonConfig().objectMapper()
    private val authenticatedUser = mockk<AuthenticatedUserArgumentResolver>()
    private val user = userWithAdmin.toAuthenticatedUser()
    private var guides = guidesBase
    private var guideService: GuideService = mockk(relaxed = true)
    private val mockMvc: MockMvc

    private val invalidGuideId = "invalid"
    private val validGuideId = 1L
    private val missingGuideId = 100L

    init {
        every { authenticatedUser.supportsParameter(any()) } returns true
        every { authenticatedUser.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(GuideController(guideService = guideService))
                .setCustomArgumentResolvers(authenticatedUser)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    private fun performGetAllGuidesRequest(params: Map<String, String> = emptyMap()): ResultActions {
        val request =
            get(Path.Guides.GET_ALL).apply {
                params.forEach { (key, value) -> param(key, value) }
            }

        return mockMvc.perform(request)
    }

    private fun assertGetAllSuccess(
        params: Map<String, String> = emptyMap(),
        expected: List<GuidePreview>,
        page: Int = 0,
        size: Int = 10,
    ) {
        val totalElements = expected.size.toLong()
        val totalPages = if (totalElements == 0L) 1 else ((totalElements + size - 1) / size).toInt()

        val expectedResponse =
            ResponseList(
                elements = expected,
                totalElements = totalElements,
                totalPages = totalPages,
                page = page,
                size = size,
            )

        every {
            guideService.getAllGuides(
                query = any(),
            )
        } returns expectedResponse

        performGetAllGuidesRequest(params)
            .andExpectSuccessResponse(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedResponse,
            )
    }

    @Nested
    inner class GetAllGuideTests {
        @Test
        fun `should return 200 if guides found on GET_ALL`() {
            val expected = guides.map { it.asPreview() }
            assertGetAllSuccess(expected = expected)
        }

        @Test
        fun `should return 200 if guides found with name filter`() {
            val expected = guides.filter { it.title.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
            assertGetAllSuccess(params = mapOf("title" to "Dog"), expected = expected)
        }

        @Test
        fun `should return 200 if guides found with sort direction ASC`() {
            val expected = guides.sortedBy { it.title }.map { it.asPreview() }
            assertGetAllSuccess(params = mapOf("sortBy" to "title", "sortDirection" to "ASC"), expected = expected)
        }
    }

    @Nested
    inner class GetGuideTests {
        @Test
        fun `should return 400 if guideId is invalid on GET`() {
            mockMvc
                .perform(
                    get(Path.Guides.GET, invalidGuideId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("id" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if guide not found on GET`() {
            every {
                guideService.getGuide(
                    id = any(),
                )
            } throws ResourceNotFoundException(ResourceType.GUIDE, missingGuideId)

            mockMvc
                .perform(
                    get(Path.Guides.GET, missingGuideId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Guide with id 100 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if guide found on GET`() {
            val expectedGuide = guides.first { it.id == validGuideId }.asPublic()

            every {
                guideService.getGuide(
                    id = expectedGuide.id,
                )
            } returns expectedGuide

            mockMvc
                .perform(
                    get(Path.Guides.GET, validGuideId),
                ).andExpectSuccessResponse<GuideInformation>(
                    expectedStatus = HttpStatus.OK,
                    expectedMessage = null,
                    expectedData = expectedGuide,
                )
        }
    }

    @Nested
    inner class CreateGuideTests {
        @Test
        fun `should return 409 if guide title exists with the same veterinarian on CREATE`() {
            val expectedGuide = guides.first { it.id == validGuideId }
            val createdGuide =
                GuideCreateInputModel(
                    title = expectedGuide.title,
                    description = expectedGuide.description,
                    content = expectedGuide.content,
                )

            val jsonPart =
                MockMultipartFile(
                    "guide",
                    "guide.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(createdGuide),
                )

            every {
                guideService.createGuide(
                    user = any(),
                    createdGuide = any(),
                    image = any(),
                )
            } throws
                ResourceAlreadyExistsException(
                    ResourceType.GUIDE,
                    "title + authorId",
                    "title='${createdGuide.title}', authorId=",
                )

            mockMvc
                .perform(
                    multipart(Path.Guides.CREATE)
                        .file(jsonPart),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.CONFLICT,
                    expectedMessage =
                        "Resource already exists: Guide with title + authorId title='Dog Care', authorId= already exists",
                    expectedErrorDetails = listOf(null to "Resource already exists"),
                )
        }

        @Test
        fun `should return 200 if guide created successfully`() {
            val expectedGuide = guides.first()
            val createdGuide =
                GuideCreateInputModel(
                    title = expectedGuide.title,
                    description = expectedGuide.description,
                    content = expectedGuide.content,
                )

            val jsonPart =
                MockMultipartFile(
                    "guide",
                    "guide.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(createdGuide),
                )

            every {
                guideService.createGuide(
                    user = any(),
                    createdGuide = any(),
                    image = any(),
                )
            } returns expectedGuide.id

            mockMvc
                .perform(
                    multipart(Path.Guides.CREATE)
                        .file(jsonPart),
                ).andExpectSuccessResponse<Map<String, Long>>(
                    expectedStatus = HttpStatus.CREATED,
                    expectedMessage = null,
                    expectedData = mapOf("id" to expectedGuide.id),
                )
        }
    }

    @Nested
    inner class UpdateGuideTests {
        @Test
        fun `should return 400 if guideId is invalid on UPDATE`() {
            val updatedGuide =
                GuideUpdateInputModel(
                    title = "Dog Care v2",
                    description = "Dog Care v2",
                    content = "Dog Care v2 content",
                )

            val jsonPart =
                MockMultipartFile(
                    "guide",
                    "guide.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedGuide),
                )

            mockMvc
                .perform(
                    multipart(Path.Guides.UPDATE, invalidGuideId)
                        .file(jsonPart)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("id" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if guide not found on UPDATE`() {
            val updatedGuide =
                GuideUpdateInputModel(
                    title = "Dog Care v2",
                    description = "Dog Care v2",
                    content = "Dog Care v2 content",
                )

            val jsonPart =
                MockMultipartFile(
                    "guide",
                    "guide.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedGuide),
                )

            every {
                guideService.updateGuide(
                    user = any(),
                    id = any(),
                    updatedGuide = any(),
                    image = any(),
                    file = any(),
                )
            } throws ResourceNotFoundException(ResourceType.GUIDE, validGuideId)

            mockMvc
                .perform(
                    multipart(Path.Guides.UPDATE, validGuideId)
                        .file(jsonPart)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Guide with id 1 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if guide updated successfully`() {
            val expectedGuide = guides.first()
            val updatedGuide =
                GuideUpdateInputModel(
                    title = "Dog Care v2",
                    description = "Dog Care v2",
                    content = "Dog Care v2 content",
                )

            val jsonPart =
                MockMultipartFile(
                    "guide",
                    "guide.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedGuide),
                )

            every {
                guideService.updateGuide(
                    user = any(),
                    id = any(),
                    updatedGuide = any(),
                    image = any(),
                    file = any(),
                )
            } returns expectedGuide.asPublic()

            mockMvc
                .perform(
                    multipart(Path.Guides.UPDATE, expectedGuide.id)
                        .file(jsonPart)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }

    @Nested
    inner class DeleteGuideTests {
        @Test
        fun `should return 400 if guideId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Guides.DELETE, invalidGuideId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("id" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if guide not found on DELETE`() {
            val guideId = 5L
            every {
                guideService.deleteGuide(
                    user = any(),
                    id = guideId,
                )
            } throws ResourceNotFoundException(ResourceType.GUIDE, guideId)

            mockMvc
                .perform(
                    delete(Path.Guides.DELETE, guideId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Guide with id 5 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 204 if guide deleted successfully`() {
            val guideId = 1L
            every {
                guideService.deleteGuide(
                    user = any(),
                    id = guideId,
                )
            } returns true

            mockMvc
                .perform(
                    delete(Path.Guides.DELETE, guideId),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }
}
