package com.cheestree.vetly.unit.controller

import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.TestUtils.daysFromNow
import com.cheestree.vetly.TestUtils.toJson
import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.http.GlobalExceptionHandler
import com.cheestree.vetly.controller.CheckupController
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.CheckupService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import java.time.OffsetDateTime
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class CheckupControllerTestBase : UnitTestBase() {
    @MockitoBean
    lateinit var userService: UserService

    private val authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()
    private val user = userWithAdmin.toAuthenticatedUser()
    private var checkups = checkupsBase
    private var checkupService: CheckupService = mockk(relaxed = true)
    private val mockMvc: MockMvc

    init {
        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(CheckupController(checkupService = checkupService))
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    private fun assertGetAllCheckupsSuccess(
        params: Map<String, String> = emptyMap(),
        expectedCheckups: List<CheckupPreview>,
        page: Int = 0,
        size: Int = 10,
    ) {
        val totalElements = expectedCheckups.size.toLong()
        val totalPages = if (totalElements == 0L) 1 else ((totalElements + size - 1) / size).toInt()

        val expectedResponse =
            ResponseList(
                elements = expectedCheckups,
                totalElements = totalElements,
                totalPages = totalPages,
                page = page,
                size = size,
            )

        every {
            checkupService.getAllCheckups(
                authenticatedUser = any(),
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
                sortDirection = any(),
            )
        } returns expectedResponse

        val requestBuilder = get(Path.Checkups.GET_ALL)
        params.forEach { (key, value) -> requestBuilder.param(key, value) }

        mockMvc
            .perform(requestBuilder)
            .andExpectSuccessResponse(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedResponse,
            )
    }

    @Nested
    inner class GetAllCheckupTests {
        @Test
        fun `should return 200 if checkups found on GET_ALL`() {
            val expectedCheckups = checkups.map { it.asPreview() }
            assertGetAllCheckupsSuccess(
                expectedCheckups = expectedCheckups,
            )
        }

        @Test
        fun `should return 200 if checkups found with name filter`() {
            val expectedCheckups =
                checkups
                    .filter { it.animal.name.contains("Dog", ignoreCase = true) }
                    .map { it.asPreview() }

            assertGetAllCheckupsSuccess(
                expectedCheckups = expectedCheckups,
                params = mapOf("animalName" to "Dog"),
            )
        }

        @Test
        fun `should return 200 if checkups found with birthDate filter`() {
            val birthDate = daysAgo(2).toString()
            val expectedCheckups =
                checkups
                    .filter { it.animal.birthDate?.isEqual(OffsetDateTime.parse(birthDate)) == true }
                    .map { it.asPreview() }

            assertGetAllCheckupsSuccess(
                expectedCheckups = expectedCheckups,
                params = mapOf("dateTimeStart" to birthDate),
            )
        }

        @Test
        fun `should return 200 if checkups found with sort by dateTimeStart and direction ASC`() {
            val expectedCheckups =
                checkups
                    .sortedBy { it.dateTime }
                    .map { it.asPreview() }

            assertGetAllCheckupsSuccess(
                expectedCheckups = expectedCheckups,
                params = mapOf("sortBy" to "dateTimeStart", "sortDirection" to "ASC"),
            )
        }
    }

    @Nested
    inner class GetCheckupTests {
        @Test
        fun `should return 400 if checkupId is invalid on GET`() {
            mockMvc
                .perform(
                    get(Path.Checkups.GET, "invalid"),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("checkupId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if checkup not found on GET`() {
            val checkupId = 1L

            every {
                checkupService.getCheckup(
                    user = any(),
                    checkupId = any(),
                )
            } throws ResourceNotFoundException(ResourceType.CHECKUP, checkupId)

            mockMvc
                .perform(
                    get(Path.Checkups.GET, checkupId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Checkup with id 1 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if checkup found on GET`() {
            val checkupId = 1L
            val expectedCheckup = checkups.first { it.id == checkupId }

            every {
                checkupService.getCheckup(
                    user = any(),
                    checkupId = any(),
                )
            } returns expectedCheckup.asPublic()

            mockMvc
                .perform(
                    get(Path.Checkups.GET, checkupId),
                ).andExpectSuccessResponse<CheckupInformation>(
                    expectedStatus = HttpStatus.OK,
                    expectedMessage = null,
                    expectedData = expectedCheckup.asPublic(),
                )
        }
    }

    @Nested
    inner class CreateCheckupTests {
        @Test
        fun `should return 200 if checkup created successfully`() {
            val expectedCheckup = checkups.first()

            every {
                checkupService.createCheckUp(
                    animalId = any(),
                    veterinarianId = any(),
                    clinicId = any(),
                    time = any(),
                    title = any(),
                    description = any(),
                    files = any(),
                )
            } returns expectedCheckup.id

            mockMvc
                .perform(
                    post(Path.Checkups.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            CheckupCreateInputModel(
                                title = "Routine",
                                description = "Routine checkup",
                                dateTime = daysAgo(),
                                clinicId = 1L,
                                veterinarianId = 1L,
                                animalId = 1L,
                                files = listOf(),
                            ).toJson(),
                        ),
                ).andExpectSuccessResponse<Map<String, Long>>(
                    expectedStatus = HttpStatus.CREATED,
                    expectedMessage = null,
                    expectedData = mapOf("id" to expectedCheckup.id),
                )
        }
    }

    @Nested
    inner class UpdateCheckupTests {
        @Test
        fun `should return 400 if checkupId is invalid on UPDATE`() {
            mockMvc
                .perform(
                    put(Path.Checkups.UPDATE, "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            CheckupUpdateInputModel(
                                title = null,
                                description = null,
                                dateTime = daysAgo(),
                                veterinarianId = null,
                            ).toJson(),
                        ),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("checkupId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if checkup not found on UPDATE`() {
            val checkupId = 1L
            every {
                checkupService.updateCheckUp(
                    veterinarianId = any(),
                    checkupId = any(),
                    dateTime = any(),
                    description = any(),
                    filesToAdd = any(),
                    filesToRemove = any(),
                )
            } throws ResourceNotFoundException(ResourceType.CHECKUP, checkupId)

            mockMvc
                .perform(
                    put(Path.Checkups.UPDATE, checkupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            CheckupUpdateInputModel(
                                dateTime = daysFromNow(1),
                            ).toJson(),
                        ),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Checkup with id 1 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if checkup updated successfully`() {
            val expectedCheckup = checkups.first()

            every {
                checkupService.updateCheckUp(
                    veterinarianId = any(),
                    checkupId = any(),
                    dateTime = any(),
                    description = any(),
                    filesToAdd = any(),
                    filesToRemove = any(),
                )
            } returns expectedCheckup.id

            mockMvc
                .perform(
                    put(Path.Checkups.UPDATE, expectedCheckup.id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            CheckupUpdateInputModel(
                                dateTime = daysAgo(),
                            ).toJson(),
                        ),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }

    @Nested
    inner class DeleteCheckupTests {
        @Test
        fun `should return 400 if checkupId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Checkups.DELETE, "invalid"),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("checkupId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if checkup not found on DELETE`() {
            val checkupId = 1L
            every {
                checkupService.deleteCheckup(
                    role = any(),
                    veterinarianId = any(),
                    checkupId = any(),
                )
            } throws ResourceNotFoundException(ResourceType.CHECKUP, checkupId)

            mockMvc
                .perform(
                    delete(Path.Checkups.DELETE, checkupId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Checkup with id 1 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 204 if checkup deleted successfully`() {
            val checkupId = 1L
            every {
                checkupService.deleteCheckup(
                    role = any(),
                    veterinarianId = any(),
                    checkupId = any(),
                )
            } returns true

            mockMvc
                .perform(
                    delete(Path.Checkups.DELETE, checkupId),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }
}
