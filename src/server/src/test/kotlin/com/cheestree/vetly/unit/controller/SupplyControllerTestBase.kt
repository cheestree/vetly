package com.cheestree.vetly.unit.controller

import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.TestUtils.toJson
import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.http.GlobalExceptionHandler
import com.cheestree.vetly.controller.SupplyController
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.SupplyService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal

class SupplyControllerTestBase : UnitTestBase() {
    @Mock
    lateinit var userService: UserService

    private val authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()
    private val user = userWithAdmin.toAuthenticatedUser()
    private var supplies = supplyClinicBase
    private var supplyService: SupplyService = mockk(relaxed = true)
    private val mockMvc: MockMvc

    private val invalidId = "invalid"
    private val clinicId = 1L
    private val validSupplyId = 101L
    private val missingSupplyId = 140L

    init {
        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(SupplyController(supplyService = supplyService))
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    private fun performGetAllSuppliesRequest(
        clinic: Boolean = false,
        clinicId: Long? = null,
        params: Map<String, String> = emptyMap(),
    ): ResultActions {
        val path =
            if (clinic) {
                requireNotNull(clinicId) { "clinicId must be provided for clinic route" }
                Path.Supplies.GET_CLINIC_SUPPLIES.replace("{clinicId}", clinicId.toString())
            } else {
                Path.Supplies.GET_ALL
            }

        val request =
            get(path).apply {
                params.forEach { (key, value) -> param(key, value) }
            }

        return mockMvc.perform(request)
    }

    private fun assertGetAllSuccess(
        clinic: Boolean = false,
        clinicId: Long? = null,
        params: Map<String, String> = emptyMap(),
        expectedSupplies: List<MedicalSupplyPreview>,
        page: Int = 0,
        size: Int = 10,
    ) {
        val totalElements = expectedSupplies.size.toLong()
        val totalPages = if (totalElements == 0L) 1 else ((totalElements + size - 1) / size).toInt()

        val expectedResponse =
            ResponseList(
                elements = expectedSupplies,
                totalElements = totalElements,
                totalPages = totalPages,
                page = page,
                size = size,
            )

        every {
            supplyService.getSupplies(
                name = any(),
                type = any(),
                page = any(),
                size = any(),
                sortBy = any(),
                sortDirection = any(),
            )
        } returns expectedResponse

        performGetAllSuppliesRequest(clinic, clinicId, params)
            .andExpectSuccessResponse(expectedStatus = HttpStatus.OK, expectedMessage = null, expectedData = expectedResponse)
    }

    @Nested
    inner class GetAllSupplyTests {
        @Test
        fun `should return 200 if supplies found on GET_ALL`() {
            val expected = supplies.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(expectedSupplies = expected)
        }

        @Test
        fun `should return 200 if supplies found with name filter`() {
            val expected = supplies.filter { it.medicalSupply.name == "Antibiotic A" }.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(params = mapOf("name" to "Antibiotic A"), expectedSupplies = expected)
        }

        @Test
        fun `should return 200 if supplies found with type filter`() {
            val expected = supplies.filter { it.medicalSupply is PillSupply }.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(params = mapOf("type" to "pill"), expectedSupplies = expected)
        }

        @Test
        fun `should return 200 if supplies found with sort by name ASC`() {
            val expected = supplies.sortedBy { it.medicalSupply.name }.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(params = mapOf("sortBy" to "name", "sortDirection" to "ASC"), expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies`() {
            val clinic = clinicsBase.first()
            val expected = supplies.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(clinic = true, clinicId = clinic.id, expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies with name filter`() {
            val clinic = clinicsBase.first()
            val expected = supplies.filter { it.medicalSupply.name == "Antibiotic A" }.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(clinic = true, clinicId = clinic.id, params = mapOf("name" to "Antibiotic A"), expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies with type filter`() {
            val clinic = clinicsBase.first()
            val expected = supplies.filter { it.medicalSupply is PillSupply }.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(clinic = true, clinicId = clinic.id, params = mapOf("type" to "pill"), expectedSupplies = expected)
        }

        @Test
        fun `vet should return 200 with clinic supplies sorted by name ASC`() {
            val clinic = clinicsBase.first()
            val expected = supplies.sortedBy { it.medicalSupply.name }.map { it.medicalSupply.asPreview() }
            assertGetAllSuccess(
                clinic = true,
                clinicId = clinic.id,
                params = mapOf("sortBy" to "name", "sortDirection" to "ASC"),
                expectedSupplies = expected,
            )
        }
    }

    @Nested
    inner class GetSupplyTests {
        @Test
        fun `should return 400 if supplyId is invalid on GET`() {
            mockMvc
                .perform(
                    get(Path.Supplies.GET_SUPPLY, invalidId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("supplyId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if supply not found on GET`() {
            every {
                supplyService.getSupply(
                    supplyId = any(),
                )
            } throws ResourceNotFoundException(ResourceType.SUPPLY, missingSupplyId)

            mockMvc
                .perform(
                    get(Path.Supplies.GET_SUPPLY, missingSupplyId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Supply with id 140 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if supply found on GET`() {
            val expectedSupply = supplies.first { it.id.medicalSupply == validSupplyId }.medicalSupply.asPublic()

            every {
                supplyService.getSupply(
                    supplyId = expectedSupply.id,
                )
            } returns expectedSupply

            mockMvc
                .perform(
                    get(Path.Supplies.GET_SUPPLY, validSupplyId),
                ).andExpectSuccessResponse<MedicalSupplyInformation>(
                    expectedStatus = HttpStatus.OK,
                    expectedMessage = null,
                    expectedData = expectedSupply,
                )
        }
    }

    @Nested
    inner class UpdateSupplyTests {
        @Test
        fun `should return 400 if supplyId is invalid on UPDATE`() {
            val updateSupply = MedicalSupplyUpdateInputModel(quantity = 10, price = BigDecimal(20.0))

            mockMvc
                .perform(
                    post(Path.Supplies.UPDATE, clinicId, "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateSupply.toJson()),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("supplyId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if supply not found on UPDATE`() {
            val updateSupply = MedicalSupplyUpdateInputModel(quantity = 10, price = BigDecimal(20.0))

            every {
                supplyService.updateSupply(
                    clinicId = clinicId,
                    supplyId = validSupplyId,
                    quantity = updateSupply.quantity,
                    price = updateSupply.price,
                )
            } throws ResourceNotFoundException(ResourceType.SUPPLY, validSupplyId)

            mockMvc
                .perform(
                    post(Path.Supplies.UPDATE, clinicId, validSupplyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateSupply.toJson()),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Supply with id 101 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if supply updated successfully`() {
            val expectedSupply = supplies.first()
            val updatedSupply = MedicalSupplyUpdateInputModel(quantity = 10, price = BigDecimal(20.0))

            every {
                supplyService.updateSupply(
                    clinicId = expectedSupply.id.clinic,
                    supplyId = expectedSupply.id.medicalSupply,
                    quantity = updatedSupply.quantity,
                    price = updatedSupply.price,
                )
            } returns expectedSupply.asPublic()

            mockMvc
                .perform(
                    post(Path.Supplies.UPDATE, clinicId, expectedSupply.id.medicalSupply)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedSupply.toJson()),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }

    @Nested
    inner class DeleteSupplyTests {
        @Test
        fun `should return 400 if clinicId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Supplies.DELETE, invalidId, "1"),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("clinicId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 400 if supplyId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Supplies.DELETE, "1", invalidId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("supplyId" to "Type mismatch: expected long"),
                )
        }

        @Test
        fun `should return 404 if supply not found on DELETE`() {
            every {
                supplyService.deleteSupply(
                    clinicId = clinicId,
                    supplyId = missingSupplyId,
                )
            } throws ResourceNotFoundException(ResourceType.SUPPLY, missingSupplyId)

            mockMvc
                .perform(
                    delete(Path.Supplies.DELETE, clinicId, missingSupplyId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Supply with id 140 not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 204 if supply deleted successfully`() {
            every {
                supplyService.deleteSupply(
                    clinicId = clinicId,
                    supplyId = validSupplyId,
                )
            } returns true

            mockMvc
                .perform(
                    delete(Path.Supplies.DELETE, clinicId, validSupplyId),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }
}
