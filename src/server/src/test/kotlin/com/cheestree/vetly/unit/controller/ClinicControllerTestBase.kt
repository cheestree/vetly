package com.cheestree.vetly.unit.controller

import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.TestUtils.toJson
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.ClinicController
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.ClinicService
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

@WebMvcTest(ClinicController::class)
class ClinicControllerTestBase: UnitTestBase() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var clinicService: ClinicService

    @MockitoBean
    lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    private lateinit var clinics: List<Clinic>
    private lateinit var user : AuthenticatedUser

    private val invalidClinicId = "invalid"
    private val validClinicId = 1L
    private val missingClinicId = 100L

    @BeforeTest
    fun setup() {
        clinics = clinicsBase

        user = veterinariansBase.first().toAuthenticatedUser()

        clinicService = mockk<ClinicService>()
        authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()

        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc = MockMvcBuilders
            .standaloneSetup(ClinicController(clinicService))
            .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    private fun performGetAllClinicsRequest(params: Map<String, String> = emptyMap()): ResultActions {
        val request = get(Path.Clinics.GET_ALL).apply {
            params.forEach { (key, value) -> param(key, value) }
        }

        return mockMvc.perform(request)
    }

    private fun assertGetAllSuccess(params: Map<String, String> = emptyMap(), expected: List<ClinicPreview>) {
        val pageable = PageRequest.of(0, 10)
        val expectedPage = PageImpl(expected, pageable, expected.size.toLong())

        every {
            clinicService.getAllClinics(
                name = any(), lat = any(), lng = any(),
                page = any(), size = any(),
                sortBy = any(), sortDirection = any()
            )
        } returns expectedPage

        performGetAllClinicsRequest(params)
            .andExpectSuccessResponse(expectedStatus = HttpStatus.OK, expectedMessage = null, expectedData = expectedPage)
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
            mockMvc.perform(
                get(Path.Clinics.GET, invalidClinicId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable",
                expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long")
            )
        }

        @Test
        fun `should return 404 if clinic not found on GET`() {
            every { clinicService.getClinic(
                clinicId = any(),
            ) } throws ResourceNotFoundException("Clinic not found")

            mockMvc.perform(
                get(Path.Clinics.GET, missingClinicId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Clinic not found",
                expectedErrorDetails = listOf(null to "Resource not found")
            )
        }

        @Test
        fun `should return 200 if clinic found on GET`() {
            val expectedClinic = clinics.first { it.id == validClinicId }

            every { clinicService.getClinic(
                clinicId = any(),
            ) } returns expectedClinic.asPublic()

            mockMvc.perform(
                get(Path.Clinics.GET, validClinicId)
            ).andExpectSuccessResponse<ClinicInformation>(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedClinic.asPublic()
            )
        }
    }

    @Nested
    inner class CreateClinicTests {
        @Test
        fun `should return 200 if clinic created successfully`() {
            val expectedClinic = clinics.first()
            val createdClinic = ClinicCreateInputModel(
                name = expectedClinic.name,
                nif = expectedClinic.nif,
                address = expectedClinic.address,
                lng = expectedClinic.longitude,
                lat = expectedClinic.latitude,
                phone = expectedClinic.phone,
                email = expectedClinic.email,
                imageUrl = expectedClinic.imageUrl,
                ownerId = expectedClinic.owner?.id
            )

            every { clinicService.createClinic(
                name = any(),
                nif = any(),
                address = any(),
                lng = any(),
                lat = any(),
                phone = any(),
                email = any(),
                imageUrl = any(),
                ownerId = any()
            ) } returns expectedClinic.id

            mockMvc.perform(
                post(Path.Clinics.CREATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createdClinic.toJson())
            ).andExpectSuccessResponse<Map<String, Long>>(
                expectedStatus = HttpStatus.CREATED,
                expectedMessage = null,
                expectedData = mapOf("id" to expectedClinic.id)
            )
        }
    }

    @Nested
    inner class UpdateClinicTests {
        @Test
        fun `should return 400 if clinicId is invalid on UPDATE`() {
            val updatedClinic = ClinicUpdateInputModel(
                name = null,
                nif = null,
                address = null,
                lng = null,
                lat = null,
                phone = null,
                email = null,
                imageUrl = null,
                ownerId = null
            )

            mockMvc.perform(
                put(Path.Clinics.UPDATE, invalidClinicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedClinic.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable",
                expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long")
            )
        }

        @Test
        fun `should return 404 if clinic not found on UPDATE`() {
            val updatedClinic = ClinicUpdateInputModel(
                name = null,
                nif = null,
                address = null,
                lng = null,
                lat = null,
                phone = null,
                email = null,
                imageUrl = null,
                ownerId = null
            )

            every { clinicService.updateClinic(
                clinicId = any(),
                name = any(),
                nif = any(),
                address = any(),
                lng = any(),
                lat = any(),
                phone = any(),
                email = any(),
                imageUrl = any(),
                ownerId = any()
            ) } throws ResourceNotFoundException("Clinic not found")

            mockMvc.perform(
                put(Path.Clinics.UPDATE, missingClinicId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedClinic.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Clinic not found",
                expectedErrorDetails = listOf(null to "Resource not found")
            )
        }

        @Test
        fun `should return 200 if clinic updated successfully`() {
            val expectedClinic = clinics.first()
            val updatedClinic = ClinicUpdateInputModel(
                name = "Test Clinic",
                nif = "123456789",
                address = "Test Street",
                lng = 10.0,
                lat = 20.0,
                phone = "912345678",
                email = "clinic@example.com",
                imageUrl = "https://image.com/logo.png",
                ownerId = expectedClinic.owner?.id
            )

            every { clinicService.updateClinic(
                clinicId = expectedClinic.id,
                name = updatedClinic.name,
                nif = updatedClinic.nif,
                address = updatedClinic.address,
                lng = updatedClinic.lng,
                lat = updatedClinic.lat,
                phone = updatedClinic.phone,
                email = updatedClinic.email,
                imageUrl = updatedClinic.imageUrl,
                ownerId = updatedClinic.ownerId
            ) } returns expectedClinic.id

            mockMvc.perform(
                put(Path.Clinics.UPDATE, expectedClinic.id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedClinic.toJson())
            ).andExpectSuccessResponse<Void>(
                expectedStatus = HttpStatus.NO_CONTENT,
                expectedMessage = null,
                expectedData = null
            )
        }
    }

    @Nested
    inner class DeleteClinicTests {
        @Test
        fun `should return 400 if clinicId is invalid on DELETE`() {
            mockMvc.perform(
                delete(Path.Clinics.DELETE, invalidClinicId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable",
                expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long")
            )
        }

        @Test
        fun `should return 404 if clinic not found on DELETE`() {
            every { clinicService.deleteClinic(
                clinicId = missingClinicId,
            ) } throws ResourceNotFoundException("Clinic not found")

            mockMvc.perform(
                delete(Path.Clinics.DELETE, missingClinicId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Clinic not found",
                expectedErrorDetails = listOf(null to "Resource not found")
            )
        }

        @Test
        fun `should return 204 if clinic deleted successfully`() {
            every { clinicService.deleteClinic(
                clinicId = validClinicId,
            ) } returns true

            mockMvc.perform(
                delete(Path.Clinics.DELETE, validClinicId)
            ).andExpectSuccessResponse<Void>(
                expectedStatus = HttpStatus.NO_CONTENT,
                expectedMessage = null,
                expectedData = null
            )
        }
    }
}