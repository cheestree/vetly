package com.cheestree.vetly.clinic.controller

import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.config.JacksonConfig
import com.cheestree.vetly.controller.ClinicController
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.http.GlobalExceptionHandler
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.input.clinic.OpeningHourInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.http.resolver.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.service.ClinicService
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

class ClinicControllerUnitTest : UnitTestBase() {
    @MockitoBean
    lateinit var userService: UserService

    private val objectMapper = JacksonConfig().objectMapper()
    private val authenticatedUser = mockk<AuthenticatedUserArgumentResolver>()
    private val user = userWithAdmin.toAuthenticatedUser()
    private var clinics = clinicsBase
    private var clinicService: ClinicService = mockk(relaxed = true)
    private val mockMvc: MockMvc

    private val invalidClinicId = "invalid"
    private val validClinicId = 1L
    private val missingClinicId = 100L

    init {
        every { authenticatedUser.supportsParameter(any()) } returns true
        every { authenticatedUser.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(ClinicController(clinicService = clinicService))
                .setCustomArgumentResolvers(authenticatedUser)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    private fun performGetAllClinicsRequest(params: Map<String, String> = emptyMap()): ResultActions {
        val request =
            get(Path.Clinics.GET_ALL).apply {
                params.forEach { (key, value) -> param(key, value) }
            }

        return mockMvc.perform(request)
    }

    private fun assertGetAllSuccess(
        params: Map<String, String> = emptyMap(),
        expected: List<ClinicPreview>,
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
            clinicService.getAllClinics(
                query = any(),
            )
        } returns expectedResponse

        performGetAllClinicsRequest(params)
            .andExpectSuccessResponse(expectedStatus = HttpStatus.OK, expectedMessage = null, expectedData = expectedResponse)
    }

    @Nested
    inner class GetAllClinicTests {
        @Test
        fun `should return 200 if clinics found on GET_ALL`() {
            val expected = clinics.map { it.asPreview() }
            assertGetAllSuccess(expected = expected)
        }

        @Test
        fun `should return 200 if clinics found with name filter`() {
            val expected = clinics.filter { it.name == "Pets" }.map { it.asPreview() }
            assertGetAllSuccess(params = mapOf("name" to "Pets"), expected = expected)
        }

        @Test
        fun `should return 200 if clinics found with lat filter`() {
            val lat = 2.0
            val expectedClinics = clinics.filter { it.latitude == lat }.map { it.asPreview() }
            assertGetAllSuccess(params = mapOf("lat" to lat.toString()), expected = expectedClinics)
        }

        @Test
        fun `should return 200 if clinics found with sort by name and direction ASC`() {
            val expectedClinics = clinics.sortedBy { it.name }.map { it.asPreview() }
            assertGetAllSuccess(params = mapOf("sortBy" to "name", "sortDirection" to "ASC"), expected = expectedClinics)
        }
    }

    @Nested
    inner class GetClinicTests {
        @Test
        fun `should return 400 if clinicId is invalid on GET`() {
            mockMvc
                .perform(
                    get(Path.Clinics.GET, invalidClinicId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if clinic not found on GET`() {
            every {
                clinicService.getClinic(
                    clinicId = any(),
                )
            } throws ResourceNotFoundException(ResourceType.CLINIC, missingClinicId)

            mockMvc
                .perform(
                    get(Path.Clinics.GET, missingClinicId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Clinic with id 100 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if clinic found on GET`() {
            val expectedClinic = clinics.first { it.id == validClinicId }

            every {
                clinicService.getClinic(
                    clinicId = any(),
                )
            } returns expectedClinic.asPublic()

            mockMvc
                .perform(
                    get(Path.Clinics.GET, validClinicId),
                ).andExpectSuccessResponse<ClinicInformation>(
                    expectedStatus = HttpStatus.OK,
                    expectedMessage = null,
                    expectedData = expectedClinic.asPublic(),
                )
        }
    }

    @Nested
    inner class CreateClinicTests {
        @Test
        fun `should return 200 if clinic created successfully`() {
            val expectedClinic = clinics.first()
            val createdClinic =
                ClinicCreateInputModel(
                    name = expectedClinic.name,
                    nif = expectedClinic.nif,
                    address = expectedClinic.address,
                    lng = expectedClinic.longitude,
                    lat = expectedClinic.latitude,
                    phone = expectedClinic.phone,
                    email = expectedClinic.email,
                    services = expectedClinic.services,
                    openingHours = expectedClinic.openingHours.map { OpeningHourInputModel(it.weekday, it.opensAt, it.closesAt) },
                    ownerEmail = expectedClinic.owner?.email,
                )

            val jsonPart =
                MockMultipartFile(
                    "clinic",
                    "clinic.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(createdClinic),
                )

            every {
                clinicService.createClinic(
                    createdClinic = any(),
                    image = any(),
                )
            } returns expectedClinic.id

            mockMvc
                .perform(
                    multipart(Path.Clinics.CREATE)
                        .file(jsonPart),
                ).andExpectSuccessResponse<Map<String, Long>>(
                    expectedStatus = HttpStatus.CREATED,
                    expectedMessage = null,
                    expectedData = mapOf("id" to expectedClinic.id),
                )
        }
    }

    @Nested
    inner class UpdateClinicTests {
        @Test
        fun `should return 400 if clinicId is invalid on UPDATE`() {
            val updatedClinic = ClinicUpdateInputModel()

            val jsonPart =
                MockMultipartFile(
                    "clinic",
                    "clinic.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedClinic),
                )

            mockMvc
                .perform(
                    multipart(Path.Clinics.UPDATE, invalidClinicId)
                        .file(jsonPart)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if clinic not found on UPDATE`() {
            val updatedClinic = ClinicUpdateInputModel()

            val jsonPart =
                MockMultipartFile(
                    "clinic",
                    "clinic.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedClinic),
                )

            every {
                clinicService.updateClinic(
                    clinicId = any(),
                    updatedClinic = any(),
                    image = any(),
                )
            } throws ResourceNotFoundException(ResourceType.CLINIC, missingClinicId)

            mockMvc
                .perform(
                    multipart(Path.Clinics.UPDATE, missingClinicId)
                        .file(jsonPart)
                        .with {
                            it.method = "PATCH"
                            it
                        },
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Clinic with id 100 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if clinic updated successfully`() {
            val expectedClinic = clinics.first()
            val updatedClinic =
                ClinicUpdateInputModel(
                    name = "Test Clinic",
                    nif = "123456789",
                    address = "Test Street",
                    lng = 10.0,
                    lat = 20.0,
                    phone = "912345678",
                    email = "clinic@example.com",
                    services = null,
                    openingHours = null,
                    ownerEmail = expectedClinic.owner?.email,
                )

            val jsonPart =
                MockMultipartFile(
                    "clinic",
                    "clinic.json",
                    "application/json",
                    objectMapper.writeValueAsBytes(updatedClinic),
                )

            every {
                clinicService.updateClinic(
                    clinicId = any(),
                    updatedClinic = any(),
                    image = any(),
                )
            } returns expectedClinic.id

            mockMvc
                .perform(
                    multipart(Path.Clinics.UPDATE, expectedClinic.id)
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
    inner class DeleteClinicTests {
        @Test
        fun `should return 400 if clinicId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Clinics.DELETE, invalidClinicId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if clinic not found on DELETE`() {
            every {
                clinicService.deleteClinic(
                    clinicId = missingClinicId,
                )
            } throws ResourceNotFoundException(ResourceType.CLINIC, missingClinicId)

            mockMvc
                .perform(
                    delete(Path.Clinics.DELETE, missingClinicId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Clinic with id 100 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 204 if clinic deleted successfully`() {
            every {
                clinicService.deleteClinic(
                    clinicId = validClinicId,
                )
            } returns true

            mockMvc
                .perform(
                    delete(Path.Clinics.DELETE, validClinicId),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }
}
