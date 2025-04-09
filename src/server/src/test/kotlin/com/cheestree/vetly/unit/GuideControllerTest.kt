package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.GuideController
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
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
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.OffsetDateTime
import java.util.*
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

    @Test
    fun `should return 200 if guides found on GET_ALL`() {
        val pageable = PageRequest.of(0, 10)
        val expectedGuides = guides.map { it.asPreview() }
        val expectedPage: Page<GuidePreview> = PageImpl(expectedGuides, pageable, expectedGuides.size.toLong())

        every { guideService.getAllGuides(
            title = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Guides.GET_ALL)
        ).andExpectSuccessResponse<Page<GuidePreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if guides found with name filter`() {
        val pageable = PageRequest.of(0, 10)
        val expectedGuides = guides.filter { it.title.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
        val expectedPage: Page<GuidePreview> = PageImpl(expectedGuides, pageable, expectedGuides.size.toLong())

        every { guideService.getAllGuides(
            title = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Guides.GET_ALL).param("title", "Dog")
        ).andExpectSuccessResponse<Page<GuidePreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if guides found with sort direction ASC`() {
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
        val expectedGuides = guides.sortedBy { it.title }.map { it.asPreview() }
        val expectedPage: Page<GuidePreview> = PageImpl(expectedGuides, pageable, expectedGuides.size.toLong())

        every { guideService.getAllGuides(
            title = any(),
            page = any(),
            size = any(),
            sortBy = "title",
            sortDirection = Sort.Direction.ASC
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Guides.GET_ALL).param("sortDirection", "ASC")
        ).andExpectSuccessResponse<Page<GuidePreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 400 if guideId is invalid on GET`() {
        mockMvc.perform(
            get(Path.Guides.GET, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: guideId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if guide not found on GET`() {
        val guideId = 5L
        every { guideService.getGuide(
            guideId = any()
        ) } throws ResourceNotFoundException("Guide not found")

        mockMvc.perform(
            get(Path.Guides.GET, guideId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Guide not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if guide found on GET`() {
        val guideId = 1L
        val expectedGuide = guides.first { it.id == guideId }

        every { guideService.getGuide(
            guideId = guideId
        ) } returns expectedGuide.asPublic()

        mockMvc.perform(
            get(Path.Guides.GET, guideId)
        ).andExpectSuccessResponse<GuideInformation>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedGuide.asPublic()
        )
    }

    @Test
    fun `should return 409 if guide title exists with the same veterinarian on CREATE`(){
        val guideId = 1L
        val expectedGuide = guides.first { it.id == guideId }

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
                .content(GuideCreateInputModel(
                    title = expectedGuide.title,
                    description = expectedGuide.description,
                    imageUrl = expectedGuide.imageUrl,
                    content = expectedGuide.content
        ).toJson())).andExpectErrorResponse(
            expectedStatus = HttpStatus.CONFLICT,
            expectedMessage = "Resource already exists: Guide with title ${expectedGuide.title} already exists for user ${expectedGuide.user.id}",
            expectedError = "Resource already exists"
        )
    }

    @Test
    fun `should return 200 if guide created successfully`() {
        val expectedGuide = guides.first()

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
                .content(GuideCreateInputModel(
                    title = expectedGuide.title,
                    description = expectedGuide.description,
                    imageUrl = expectedGuide.imageUrl,
                    content = expectedGuide.content
                ).toJson())
        ).andExpectSuccessResponse<Map<String, Long>>(
            expectedStatus = HttpStatus.CREATED,
            expectedMessage = null,
            expectedData = mapOf("id" to expectedGuide.id)
        )
    }

    @Test
    fun `should return 400 if guideId is invalid on UPDATE`() {
        mockMvc.perform(
            put(Path.Guides.UPDATE, "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(GuideUpdateInputModel(
                    title = "Dog Care v2",
                    description = null,
                    imageUrl = null,
                    content = null,
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: guideId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if guide not found on UPDATE`() {
        val guideId = 1L
        every { guideService.updateGuide(
            veterinarianId = any(),
            roles = any(),
            guideId = any(),
            title = any(),
            description = any(),
            imageUrl = any(),
            content = any()
        ) } throws ResourceNotFoundException("Guide not found")

        mockMvc.perform(
            put(Path.Guides.UPDATE, guideId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(GuideUpdateInputModel(
                    title = "Dog Care v2",
                    description = null,
                    imageUrl = null,
                    content = null,
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Guide not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if guide updated successfully`() {
        val expectedGuide = guides.first()

        every { guideService.updateGuide(
            veterinarianId = any(),
            roles = any(),
            guideId = any(),
            title = any(),
            description = any(),
            imageUrl = any(),
            content = any()
        ) } returns expectedGuide.asPublic()

        mockMvc.perform(
            put(Path.Guides.UPDATE, expectedGuide.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(GuideUpdateInputModel(
                    title = expectedGuide.title,
                    description = expectedGuide.description,
                    imageUrl = expectedGuide.imageUrl,
                    content = expectedGuide.content
                ).toJson())
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }

    @Test
    fun `should return 400 if guideId is invalid on DELETE`() {
        mockMvc.perform(
            delete(Path.Guides.DELETE, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: guideId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if guide not found on DELETE`() {
        val guideId = 1L
        every { guideService.deleteGuide(
            veterinarianId = any(),
            roles = any(),
            guideId = any()
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
            guideId = any()
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