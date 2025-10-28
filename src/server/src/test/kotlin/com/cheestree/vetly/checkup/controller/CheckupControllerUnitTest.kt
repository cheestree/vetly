package com.cheestree.vetly.checkup.controller

import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.TestUtils.daysFromNow
import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.config.JacksonConfig
import com.cheestree.vetly.controller.CheckupController
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.http.GlobalExceptionHandler
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.http.resolver.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.service.CheckupService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.openapitools.jackson.nullable.JsonNullable
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.OffsetDateTime

class CheckupControllerUnitTest : UnitTestBase() {
    @MockitoBean
    lateinit var userService: UserService

    private val mockMvc: MockMvc

    private val objectMapper = JacksonConfig().objectMapper()
    private val authenticatedUser = mockk<AuthenticatedUserArgumentResolver>()
    private val user = userWithAdmin.toAuthenticatedUser()
    private var checkups = checkupsBase
    private var checkupService: CheckupService = mockk(relaxed = true)

    init {
        every { authenticatedUser.supportsParameter(any()) } returns true
        every { authenticatedUser.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(CheckupController(checkupService = checkupService))
                .setCustomArgumentResolvers(authenticatedUser)
                .setControllerAdvice(GlobalExceptionHandler())
                .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
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
                user = any(),
                query = any(),
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
                    expectedErrorDetails = listOf("id" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if checkup not found on GET`() {
            val checkupId = 1L

            every {
                checkupService.getCheckup(
                    user = any(),
                    id = any(),
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
                    id = any(),
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
            val updatedCheckup =
                CheckupCreateInputModel(
                    title = "Routine",
                    description = "Routine checkup",
                    dateTime = daysAgo(),
                    clinicId = 1L,
                    veterinarianId = expectedCheckup.veterinarian.publicId,
                    animalId = 1L,
                )

            val jsonPart =
                MockMultipartFile(
                    "checkup",
                    "checkup.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedCheckup),
                )

            every {
                checkupService.createCheckUp(
                    user = any(),
                    createdCheckup = any(),
                    files = any(),
                )
            } returns expectedCheckup.id

            mockMvc
                .perform(
                    multipart(Path.Checkups.CREATE)
                        .file(jsonPart),
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
            val updatedCheckup =
                CheckupUpdateInputModel(
                    title = JsonNullable.undefined(),
                    description = JsonNullable.undefined(),
                    dateTime = JsonNullable.of(daysFromNow()),
                )

            val jsonPart =
                MockMultipartFile(
                    "checkup",
                    "checkup.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedCheckup),
                )

            mockMvc
                .perform(
                    multipart(Path.Checkups.UPDATE, "invalid")
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
        fun `should return 404 if checkup not found on UPDATE`() {
            val checkupId = 1L
            val updatedCheckup =
                CheckupUpdateInputModel(
                    title = JsonNullable.undefined(),
                    description = JsonNullable.undefined(),
                    dateTime = JsonNullable.of(daysFromNow()),
                )

            val jsonPart =
                MockMultipartFile(
                    "checkup",
                    "checkup.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedCheckup),
                )

            every {
                checkupService.updateCheckUp(
                    user = any(),
                    id = any(),
                    updatedCheckup = any(),
                    filesToAdd = any(),
                    filesToRemove = any(),
                )
            } throws ResourceNotFoundException(ResourceType.CHECKUP, checkupId)

            mockMvc
                .perform(
                    multipart(Path.Checkups.UPDATE, checkupId)
                        .file(jsonPart)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Checkup with id 1 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if checkup updated successfully`() {
            val expectedCheckup = checkups.first()
            val updatedCheckup =
                CheckupUpdateInputModel(
                    dateTime = JsonNullable.of(daysAgo()),
                )

            val jsonPart =
                MockMultipartFile(
                    "checkup",
                    "checkup.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedCheckup),
                )

            every {
                checkupService.updateCheckUp(
                    user = any(),
                    id = any(),
                    updatedCheckup = any(),
                    filesToAdd = any(),
                    filesToRemove = any(),
                )
            } returns expectedCheckup.asPublic()

            mockMvc
                .perform(
                    multipart(Path.Checkups.UPDATE, expectedCheckup.id)
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
    inner class DeleteCheckupTests {
        @Test
        fun `should return 400 if checkupId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Checkups.DELETE, "invalid"),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("id" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if checkup not found on DELETE`() {
            val checkupId = 1L
            every {
                checkupService.deleteCheckup(
                    user = any(),
                    id = any(),
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
                    user = any(),
                    id = any(),
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
