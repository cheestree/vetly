package com.cheestree.vetly.unit.controller

import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.TestUtils.toJson
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.SupplyController
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.SupplyService
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
import java.math.BigDecimal
import kotlin.test.BeforeTest

@WebMvcTest(SupplyController::class)
class SupplyControllerTestBase: UnitTestBase() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var supplyService: SupplyService

    @MockitoBean
    lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    private lateinit var user: AuthenticatedUser
    private lateinit var supplies: List<MedicalSupplyClinic>

    private val invalidId = "invalid"
    private val clinicId = 1L
    private val validSupplyId = 101L
    private val missingSupplyId = 140L

    @BeforeTest
    fun setup() {
        supplies = supplyClinicBase

        user = userWithAdmin.toAuthenticatedUser()

        supplyService = mockk<SupplyService>()
        authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()

        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc = MockMvcBuilders
            .standaloneSetup(SupplyController(supplyService))
            .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    private fun performGetAllSuppliesRequest(
        clinic: Boolean = false,
        clinicId: Long? = null,
        params: Map<String, String> = emptyMap()
    ): ResultActions {
        val path = if (clinic) {
            requireNotNull(clinicId) { "clinicId must be provided for clinic route" }
            Path.Supplies.GET_CLINIC_SUPPLIES.replace("{clinicId}", clinicId.toString())
        } else {
            Path.Supplies.GET_ALL
        }

        val request = get(path).apply {
            params.forEach { (key, value) -> param(key, value) }
        }

        return mockMvc.perform(request)
    }

    private fun assertGetAllSuccess(
        clinic: Boolean = false,
        clinicId: Long? = null,
        params: Map<String, String> = emptyMap(),
        expectedSupplies: List<MedicalSupplyInformation>
    ) {
        val pageable = PageRequest.of(0, 10)
        val expectedPage = PageImpl(expectedSupplies, pageable, expectedSupplies.size.toLong())

        every {
            supplyService.getSupplies(
                clinicId = if (clinic) clinicId else null,
                name = any(), type = any(), page = any(), size = any(),
                sortBy = any(), sortDirection = any()
            )
        } returns expectedPage

        performGetAllSuppliesRequest(clinic, clinicId, params)
            .andExpectSuccessResponse(expectedStatus = HttpStatus.OK, expectedMessage = null, expectedData = expectedPage)
    }

    @Nested
    inner class GetAllSupplyTests {
        @Test
        fun `should return 200 if supplies found on GET_ALL`() {
            val expected = supplies.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(expectedSupplies = expected)
        }

        @Test
        fun `should return 200 if supplies found with name filter`() {
            val expected = supplies.filter { it.medicalSupply.name == "Antibiotic A" }.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(params = mapOf("name" to "Antibiotic A"), expectedSupplies = expected)
        }

        @Test
        fun `should return 200 if supplies found with type filter`() {
            val expected = supplies.filter { it.medicalSupply is PillSupply }.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(params = mapOf("type" to "pill"), expectedSupplies = expected)
        }

        @Test
        fun `should return 200 if supplies found with sort by name ASC`() {
            val expected = supplies.sortedBy { it.medicalSupply.name }.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(params = mapOf("sortBy" to "name", "sortDirection" to "ASC"), expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies`() {
            val clinic = clinicsBase.first()
            val expected = supplies.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(clinic = true, clinicId = clinic.id, expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies with name filter`() {
            val clinic = clinicsBase.first()
            val expected = supplies.filter { it.medicalSupply.name == "Antibiotic A" }.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(clinic = true, clinicId = clinic.id, params = mapOf("name" to "Antibiotic A"), expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies with type filter`() {
            val clinic = clinicsBase.first()
            val expected = supplies.filter { it.medicalSupply is PillSupply }.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(clinic = true, clinicId = clinic.id, params = mapOf("type" to "pill"), expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies sorted by name ASC`() {
            val clinic = clinicsBase.first()
            val expected = supplies.sortedBy { it.medicalSupply.name }.map { it.medicalSupply.asPublic() }
            assertGetAllSuccess(clinic = true, clinicId = clinic.id, params = mapOf("sortBy" to "name", "sortDirection" to "ASC"), expectedSupplies = expected)
        }
    }

    @Nested
    inner class GetSupplyTests {
        @Test
        fun `should return 400 if supplyId is invalid on GET`() {
            mockMvc.perform(
                get(Path.Supplies.GET_SUPPLY, invalidId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable",
                expectedErrorDetails = listOf("supplyId" to "Type mismatch: expected long")
            )
        }

        @Test
        fun `should return 404 if supply not found on GET`() {
            every { supplyService.getSupply(
                supplyId = any()
            ) } throws ResourceNotFoundException("Supply not found")

            mockMvc.perform(
                get(Path.Supplies.GET_SUPPLY, missingSupplyId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Supply not found",
                expectedErrorDetails = listOf(null to "Resource not found")
            )
        }

        @Test
        fun `should return 200 if supply found on GET`() {
            val expectedSupply = supplies.first { it.id.medicalSupply == validSupplyId }.medicalSupply.asPublic()

            every { supplyService.getSupply(
                supplyId = expectedSupply.id
            ) } returns expectedSupply

            mockMvc.perform(
                get(Path.Supplies.GET_SUPPLY, validSupplyId)
            ).andExpectSuccessResponse<MedicalSupplyInformation>(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedSupply
            )
        }
    }

    @Nested
    inner class UpdateSupplyTests {
        @Test
        fun `should return 400 if supplyId is invalid on UPDATE`() {
            val updateSupply = MedicalSupplyUpdateInputModel(quantity = 10, price = BigDecimal(20.0))

            mockMvc.perform(
                post(Path.Supplies.UPDATE, clinicId, "invalid")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateSupply.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable",
                expectedErrorDetails = listOf("supplyId" to "Type mismatch: expected long")
            )
        }

        @Test
        fun `should return 404 if supply not found on UPDATE`() {
            val updateSupply = MedicalSupplyUpdateInputModel(quantity = 10, price = BigDecimal(20.0))

            every { supplyService.updateSupply(
                clinicId = clinicId,
                supplyId = validSupplyId,
                quantity = updateSupply.quantity,
                price = updateSupply.price
            ) } throws ResourceNotFoundException("Supply not found")

            mockMvc.perform(
                post(Path.Supplies.UPDATE, clinicId, validSupplyId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateSupply.toJson())
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Supply not found",
                expectedErrorDetails = listOf(null to "Resource not found")
            )
        }

        @Test
        fun `should return 200 if supply updated successfully`() {
            val expectedSupply = supplies.first()
            val updatedSupply = MedicalSupplyUpdateInputModel(quantity = 10, price = BigDecimal(20.0))

            every { supplyService.updateSupply(
                clinicId = expectedSupply.id.clinic,
                supplyId = expectedSupply.id.medicalSupply,
                quantity = updatedSupply.quantity,
                price = updatedSupply.price
            ) } returns expectedSupply.asPublic()

            mockMvc.perform(
                post(Path.Supplies.UPDATE, clinicId, expectedSupply.id.medicalSupply)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedSupply.toJson())
            ).andExpectSuccessResponse<Void>(
                expectedStatus = HttpStatus.NO_CONTENT,
                expectedMessage = null,
                expectedData = null
            )
        }
    }

    @Nested
    inner class DeleteSupplyTests {
        @Test
        fun `should return 400 if clinicId is invalid on DELETE`() {
            mockMvc.perform(
                delete(Path.Supplies.DELETE, invalidId, "1")
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable",
                expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long")
            )
        }

        @Test
        fun `should return 400 if supplyId is invalid on DELETE`() {
            mockMvc.perform(
                delete(Path.Supplies.DELETE, "1", invalidId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedMessage = "Invalid value for path variable",
                expectedErrorDetails = listOf("supplyId" to "Type mismatch: expected long")
            )
        }

        @Test
        fun `should return 404 if supply not found on DELETE`() {
            every { supplyService.deleteSupply(
                clinicId = clinicId,
                supplyId = missingSupplyId
            ) } throws ResourceNotFoundException("Supply not found")

            mockMvc.perform(
                delete(Path.Supplies.DELETE, clinicId, missingSupplyId)
            ).andExpectErrorResponse(
                expectedStatus = HttpStatus.NOT_FOUND,
                expectedMessage = "Not found: Supply not found",
                expectedErrorDetails = listOf(null to "Resource not found")
            )
        }

        @Test
        fun `should return 204 if supply deleted successfully`() {
            every { supplyService.deleteSupply(
                clinicId = clinicId,
                supplyId = validSupplyId
            ) } returns true

            mockMvc.perform(
                delete(Path.Supplies.DELETE, clinicId, validSupplyId)
            ).andExpectSuccessResponse<Void>(
                expectedStatus = HttpStatus.NO_CONTENT,
                expectedMessage = null,
                expectedData = null
            )
        }
    }
}