package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.SupplyController
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.SupplyService
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
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.OffsetDateTime
import kotlin.test.BeforeTest

@WebMvcTest(SupplyController::class)
class SupplyControllerTest: BaseTest() {

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


    @Test
    fun `should return 200 if supplies found on GET_ALL`() {
        val pageable = PageRequest.of(0, 10)
        val expectedSupplies = supplies.map { it.medicalSupply.asPublic() }
        val expectedPage: Page<MedicalSupplyInformation> = PageImpl(expectedSupplies, pageable, expectedSupplies.size.toLong())

        every { supplyService.getAllSupplies(
            name = any(),
            type = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Supplies.GET_ALL)
        ).andExpectSuccessResponse<Page<MedicalSupplyInformation>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if supplies found with name filter`() {
        val pageable = PageRequest.of(0, 10)
        val expectedSupplies = supplies.filter { it.medicalSupply.name == "Antibiotic A" }.map { it.medicalSupply.asPublic() }
        val expectedPage: Page<MedicalSupplyInformation> = PageImpl(expectedSupplies, pageable, expectedSupplies.size.toLong())

        every { supplyService.getAllSupplies(
            name = any(),
            type = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Supplies.GET_ALL).param("name", "Antibiotic A")
        ).andExpectSuccessResponse<Page<MedicalSupplyInformation>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if supplies found with type filter`() {
        val pageable = PageRequest.of(0, 10)
        val typeFilter = "pill"
        val expectedSupplies = supplies
            .filter {
                when (typeFilter) {
                    "pill" -> it.medicalSupply is PillSupply
                    "liquid" -> it.medicalSupply is LiquidSupply
                    "shot" -> it.medicalSupply is ShotSupply
                    else -> false
                }
            }
            .map { it.medicalSupply.asPublic() }
        val expectedPage: Page<MedicalSupplyInformation> = PageImpl(expectedSupplies, pageable, expectedSupplies.size.toLong())

        every { supplyService.getAllSupplies(
            name = any(),
            type = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Supplies.GET_ALL)
                .param("type", typeFilter)
        ).andExpectSuccessResponse<Page<MedicalSupplyInformation>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if supplies found with sort by dateTimeStart and direction ASC`() {
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "dateTimeStart"))
        val expectedSupplies = supplies.sortedBy { it.medicalSupply.name }.map { it.medicalSupply.asPublic() }
        val expectedPage: Page<MedicalSupplyInformation> = PageImpl(expectedSupplies, pageable, expectedSupplies.size.toLong())

        every { supplyService.getAllSupplies(
            name = any(),
            type = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = Sort.Direction.ASC
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Supplies.GET_ALL)
                .param("sortBy", "name")
                .param("sortDirection", "ASC")
        ).andExpectSuccessResponse<Page<MedicalSupplyInformation>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }


    @Test
    fun `should return 400 if supplyId is invalid on GET`() {
        mockMvc.perform(
            get(Path.Supplies.GET_SUPPLY, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: supplyId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if supply not found on GET`() {
        val supplyId = 140L

        every { supplyService.getSupply(
            supplyId = any()
        ) } throws ResourceNotFoundException("Supply not found")

        mockMvc.perform(
            get(Path.Supplies.GET_SUPPLY, supplyId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Supply not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if supply found on GET`() {
        val supplyId = 101L
        val expectedSupply = supplies.first { it.id.medicalSupply == supplyId }.medicalSupply.asPublic()

        every { supplyService.getSupply(
            supplyId = any()
        ) } returns expectedSupply

        mockMvc.perform(
            get(Path.Supplies.GET_SUPPLY, supplyId)
        ).andExpectSuccessResponse<MedicalSupplyInformation>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedSupply
        )
    }

    @Test
    fun `should return 400 if clinicId is invalid on DELETE`() {
        mockMvc.perform(
            delete(Path.Supplies.DELETE, "invalid", "1")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: clinicId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 400 if supplyId is invalid on DELETE`() {
        mockMvc.perform(
            delete(Path.Supplies.DELETE, "1", "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: supplyId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if supply not found on DELETE`() {
        val clinicId = 1L
        val supplyId = 5L
        every { supplyService.deleteSupply(
            clinicId = any(),
            supplyId = any()
        ) } throws ResourceNotFoundException("Supply not found")

        mockMvc.perform(
            delete(Path.Supplies.DELETE, clinicId, supplyId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Supply not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 204 if supply deleted successfully`() {
        val clinicId = 1L
        val supplyId = 1L
        every { supplyService.deleteSupply(
            clinicId = any(),
            supplyId = any()
        ) } returns true

        mockMvc.perform(
            delete(Path.Supplies.DELETE, clinicId, supplyId)
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }
}