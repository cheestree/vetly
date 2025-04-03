package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.ClinicController
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
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

@WebMvcTest(ClinicController::class)
class ClinicControllerTest: BaseTest() {

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

    @BeforeTest
    fun setup() {
        val veterinarianRole = RoleEntity(id = 1L, role = Role.VETERINARIAN)

        val user1 = User(1L, UUID.randomUUID(), "", "Dr. John Doe", "john.doe@example.com", roles = emptySet())

        val userRole1 = UserRole(id = UserRoleId(userId = user1.id, roleId = veterinarianRole.id), user = user1, role = veterinarianRole)

        val userWithRole1 = User(user1.id, user1.uuid, username = user1.username, email = user1.email, roles = setOf(userRole1))

        clinics = listOf(
            Clinic(1L, "", "Happy Pets Clinic", "123 Pet Street", 1.0, 1.0, "1234567890", "a@gmail.com"),
            Clinic(2L, "", "Healthy Animals Clinic", "456 Animal Avenue", 1.0, 2.0, "1234567880", "b@gmail.com")
        )

        user = userWithRole1.toAuthenticatedUser()

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

    @Test
    fun `should return 200 if clinics found on GET_ALL`() {
        val pageable = PageRequest.of(0, 10)
        val expectedClinics = clinics.map { it.asPreview() }
        val expectedPage: Page<ClinicPreview> = PageImpl(expectedClinics, pageable, expectedClinics.size.toLong())

        every { clinicService.getAllClinics(
            name = any(),
            lat = any(),
            lng = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Clinics.GET_ALL)
        ).andExpectSuccessResponse<Page<ClinicPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if clinics found with name filter`() {
        val pageable = PageRequest.of(0, 10)
        val expectedClinics = clinics.filter { it.name == "Pets" }.map { it.asPreview() }
        val expectedPage: Page<ClinicPreview> = PageImpl(expectedClinics, pageable, expectedClinics.size.toLong())

        every { clinicService.getAllClinics(
            name = any(),
            lat = any(),
            lng = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Clinics.GET_ALL)
                .param("name", "Pets")
        ).andExpectSuccessResponse<Page<ClinicPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if clinics found with birthDate filter`() {
        val lat = 2.0
        val pageable = PageRequest.of(0, 10)
        val expectedClinics = clinics.filter { it.lat == lat }.map { it.asPreview() }
        val expectedPage: Page<ClinicPreview> = PageImpl(expectedClinics, pageable, expectedClinics.size.toLong())

        every { clinicService.getAllClinics(
            name = any(),
            lat = any(),
            lng = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Clinics.GET_ALL)
                .param("lat", lat.toString())
        ).andExpectSuccessResponse<Page<ClinicPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if clinics found with sort by dateTimeStart and direction ASC`() {
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))
        val expectedClinics = clinics.sortedBy { it.name }.map { it.asPreview() }
        val expectedPage: Page<ClinicPreview> = PageImpl(expectedClinics, pageable, expectedClinics.size.toLong())

        every { clinicService.getAllClinics(
            name = any(),
            lat = any(),
            lng = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Clinics.GET_ALL)
                .param("sortBy", "name")
                .param("sortDirection", "ASC")
        ).andExpectSuccessResponse<Page<ClinicPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 400 if clinicId is invalid on GET`() {
        mockMvc.perform(
            get(Path.Clinics.GET, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: clinicId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if clinic not found on GET`() {
        val clinicId = 1L

        every { clinicService.getClinic(
            clinicId = any(),
        ) } throws ResourceNotFoundException("Clinic not found")

        mockMvc.perform(
            get(Path.Clinics.GET, clinicId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Clinic not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if clinic found on GET`() {
        val clinicId = 1L
        val expectedClinic = clinics.first { it.id == clinicId }

        every { clinicService.getClinic(
            clinicId = any(),
        ) } returns expectedClinic.asPublic()

        mockMvc.perform(
            get(Path.Clinics.GET, clinicId)
        ).andExpectSuccessResponse<ClinicInformation>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedClinic.asPublic()
        )
    }

    @Test
    fun `should return 200 if clinic created successfully`() {
        val expectedClinic = clinics.first()

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
                .content(ClinicCreateInputModel(
                    name = expectedClinic.name,
                    nif = expectedClinic.nif,
                    address = expectedClinic.address,
                    lng = expectedClinic.lng,
                    lat = expectedClinic.lat,
                    phone = expectedClinic.phone,
                    email = expectedClinic.email,
                    imageUrl = expectedClinic.imageUrl,
                    ownerId = expectedClinic.owner?.id
                ).toJson())
        ).andExpectSuccessResponse<Map<String, Long>>(
            expectedStatus = HttpStatus.CREATED,
            expectedMessage = null,
            expectedData = mapOf("id" to expectedClinic.id)
        )
    }

    @Test
    fun `should return 400 if clinicId is invalid on UPDATE`() {
        mockMvc.perform(
            put(Path.Clinics.UPDATE, "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ClinicUpdateInputModel(
                    name = null,
                    nif = null,
                    address = null,
                    lng = null,
                    lat = null,
                    phone = null,
                    email = null,
                    imageUrl = null,
                    ownerId = null
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: clinicId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if clinic not found on UPDATE`() {
        val clinicId = 1L
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
            put(Path.Clinics.UPDATE, clinicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ClinicUpdateInputModel(
                    name = null,
                    nif = null,
                    address = null,
                    lng = null,
                    lat = null,
                    phone = null,
                    email = null,
                    imageUrl = null,
                    ownerId = null
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Clinic not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if clinic updated successfully`() {
        val expectedClinic = clinics.first()

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
        ) } returns expectedClinic.id

        mockMvc.perform(
            put(Path.Clinics.UPDATE, expectedClinic.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ClinicUpdateInputModel(
                    name = expectedClinic.name,
                    nif = expectedClinic.nif,
                    address = expectedClinic.address,
                    lng = expectedClinic.lng,
                    lat = expectedClinic.lat,
                    phone = expectedClinic.phone,
                    email = expectedClinic.email,
                    imageUrl = expectedClinic.imageUrl,
                    ownerId = expectedClinic.owner?.id
                ).toJson())
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }

    @Test
    fun `should return 400 if clinicId is invalid on DELETE`() {
        mockMvc.perform(
            delete(Path.Clinics.DELETE, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: clinicId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if clinic not found on DELETE`() {
        val guideId = 1L
        every { clinicService.deleteClinic(
            clinicId = any(),
        ) } throws ResourceNotFoundException("Clinic not found")

        mockMvc.perform(
            delete(Path.Clinics.DELETE, guideId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Clinic not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 204 if clinic deleted successfully`() {
        val clinicId = 1L
        every { clinicService.deleteClinic(
            clinicId = any(),
        ) } returns true

        mockMvc.perform(
            delete(Path.Clinics.DELETE, clinicId)
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }
}