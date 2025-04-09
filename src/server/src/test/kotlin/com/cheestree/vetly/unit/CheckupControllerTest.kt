package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.CheckupController
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.CheckupService
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

@WebMvcTest(CheckupController::class)
class CheckupControllerTest: BaseTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var checkupService: CheckupService

    @MockitoBean
    lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    private lateinit var checkups: List<Checkup>
    private lateinit var animals: List<Animal>
    private lateinit var veterinarians: List<User>
    private lateinit var user : AuthenticatedUser

    @BeforeTest
    fun setup() {
        animals = animalsBase

        veterinarians = veterinariansBase

        user = veterinariansBase.first().toAuthenticatedUser()

        checkups = checkupsBase

        checkupService = mockk<CheckupService>()
        authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()

        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc = MockMvcBuilders
            .standaloneSetup(CheckupController(checkupService))
            .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `should return 200 if checkups found on GET_ALL`() {
        val pageable = PageRequest.of(0, 10)
        val expectedCheckups = checkups.map { it.asPreview() }
        val expectedPage: Page<CheckupPreview> = PageImpl(expectedCheckups, pageable, expectedCheckups.size.toLong())

        every { checkupService.getAllCheckups(
            veterinarianId = any(),
            animalId = any(),
            clinicId = any(),
            dateTimeStart = any(),
            dateTimeEnd = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Checkups.GET_ALL)
        ).andExpectSuccessResponse<Page<CheckupPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if checkups found with name filter`() {
        val pageable = PageRequest.of(0, 10)
        val expectedCheckups = checkups.filter { it.animal.name.contains("Dog", ignoreCase = true) }.map { it.asPreview() }
        val expectedPage: Page<CheckupPreview> = PageImpl(expectedCheckups, pageable, expectedCheckups.size.toLong())

        every { checkupService.getAllCheckups(
            veterinarianId = any(),
            veterinarianName = any(),
            animalId = any(),
            animalName = any(),
            clinicId = any(),
            clinicName = any(),
            dateTimeStart = any(),
            dateTimeEnd = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Checkups.GET_ALL).param("animalName", "Dog")
        ).andExpectSuccessResponse<Page<CheckupPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if checkups found with birthDate filter`() {
        val pageable = PageRequest.of(0, 10)
        val birthDate = OffsetDateTime.now().minusDays(2).toString()
        val expectedCheckups = checkups.filter { it.animal.birthDate?.isEqual(OffsetDateTime.parse(birthDate)) ?: false }.map { it.asPreview() }
        val expectedPage: Page<CheckupPreview> = PageImpl(expectedCheckups, pageable, expectedCheckups.size.toLong())

        every { checkupService.getAllCheckups(
            veterinarianId = any(),
            animalId = any(),
            clinicId = any(),
            dateTimeStart = match { it == OffsetDateTime.parse(birthDate) },
            dateTimeEnd = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Checkups.GET_ALL)
                .param("dateTimeStart", birthDate)
        ).andExpectSuccessResponse<Page<CheckupPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if checkups found with sort by dateTimeStart and direction ASC`() {
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dateTimeStart"))
        val expectedCheckups = checkups.sortedBy { it.dateTime }.map { it.asPreview() }
        val expectedPage: Page<CheckupPreview> = PageImpl(expectedCheckups, pageable, expectedCheckups.size.toLong())

        every { checkupService.getAllCheckups(
            veterinarianId = any(),
            veterinarianName = any(),
            animalId = any(),
            animalName = any(),
            clinicId = any(),
            clinicName = any(),
            dateTimeStart = any(),
            dateTimeEnd = any(),
            page = any(),
            size = any(),
            sortBy = "dateTimeStart",
            sortDirection = Sort.Direction.ASC
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Checkups.GET_ALL)
                .param("sortBy", "dateTimeStart")
                .param("sortDirection", "ASC")
        ).andExpectSuccessResponse<Page<CheckupPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 400 if checkupId is invalid on GET`() {
        mockMvc.perform(
            get(Path.Checkups.GET, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: checkupId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if checkup not found on GET`() {
        val checkupId = 1L

        every { checkupService.getCheckup(
            userId = any(),
            checkupId = any()
        ) } throws ResourceNotFoundException("Checkup not found")

        mockMvc.perform(
            get(Path.Checkups.GET, checkupId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Checkup not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if checkup found on GET`() {
        val checkupId = 1L
        val expectedCheckup = checkups.first { it.id == checkupId }

        every { checkupService.getCheckup(
            userId = any(),
            checkupId = any()
        ) } returns expectedCheckup.asPublic()

        mockMvc.perform(
            get(Path.Checkups.GET, checkupId)
        ).andExpectSuccessResponse<CheckupInformation>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedCheckup.asPublic()
        )
    }

    @Test
    fun `should return 200 if checkup created successfully`() {
        val expectedCheckup = checkups.first()

        every { checkupService.createCheckUp(
            animalId = any(),
            veterinarianId = any(),
            clinicId = any(),
            time = any(),
            description = any(),
            files = any()
        ) } returns expectedCheckup.id

        mockMvc.perform(
            post(Path.Checkups.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CheckupCreateInputModel(
                    description = "Routine checkup",
                    dateTime = OffsetDateTime.now(),
                    clinicId = 1L,
                    veterinarianId = 1L,
                    animalId = 1L,
                    files = listOf()
                ).toJson())
        ).andExpectSuccessResponse<Map<String, Long>>(
            expectedStatus = HttpStatus.CREATED,
            expectedMessage = null,
            expectedData = mapOf("id" to expectedCheckup.id)
        )
    }

    @Test
    fun `should return 400 if checkupId is invalid on UPDATE`() {
        mockMvc.perform(
            put(Path.Checkups.UPDATE, "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CheckupUpdateInputModel(
                    description = null,
                    dateTime = OffsetDateTime.now(),
                    veterinarianId = null,
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: checkupId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if checkup not found on UPDATE`() {
        val checkupId = 1L
        every { checkupService.updateCheckUp(
            veterinarianId = any(),
            checkupId = any(),
            updatedVetId = any(),
            updatedTime = any(),
            updatedDescription = any(),
        ) } throws ResourceNotFoundException("Checkup not found")

        mockMvc.perform(
            put(Path.Checkups.UPDATE, checkupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CheckupUpdateInputModel(
                    dateTime = OffsetDateTime.now()
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Checkup not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if checkup updated successfully`() {
        val expectedCheckup = checkups.first()

        every { checkupService.updateCheckUp(
            veterinarianId = any(),
            checkupId = any(),
            updatedVetId = any(),
            updatedTime = any(),
            updatedDescription = any(),
        ) } returns expectedCheckup.id

        mockMvc.perform(
            put(Path.Checkups.UPDATE, expectedCheckup.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CheckupUpdateInputModel(
                    dateTime = OffsetDateTime.now()
                ).toJson())
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }

    @Test
    fun `should return 400 if checkupId is invalid on DELETE`() {
        mockMvc.perform(
            delete(Path.Checkups.DELETE, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: checkupId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if checkup not found on DELETE`() {
        val guideId = 1L
        every { checkupService.deleteCheckup(
            role = any(),
            veterinarianId = any(),
            checkupId = any()
        ) } throws ResourceNotFoundException("Checkup not found")

        mockMvc.perform(
            delete(Path.Checkups.DELETE, guideId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Checkup not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 204 if checkup deleted successfully`() {
        val checkupId = 1L
        every { checkupService.deleteCheckup(
            role = any(),
            veterinarianId = any(),
            checkupId = any()
        ) } returns true

        mockMvc.perform(
            delete(Path.Checkups.DELETE, checkupId)
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }
}