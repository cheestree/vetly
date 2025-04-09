package com.cheestree.vetly.unit

import com.cheestree.vetly.BaseTest
import com.cheestree.vetly.BaseTest.Companion.toJson
import com.cheestree.vetly.advice.GlobalExceptionHandler
import com.cheestree.vetly.controller.RequestController
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.RequestService
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

@WebMvcTest(RequestController::class)
class RequestControllerTest: BaseTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var userService: UserService

    @MockitoBean
    lateinit var requestService: RequestService

    @MockitoBean
    lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    private lateinit var requests: List<Request>
    private lateinit var user: AuthenticatedUser

    @BeforeTest
    fun setup() {
        requests = requestsBase

        user = userWithAdmin.toAuthenticatedUser()

        requestService = mockk<RequestService>()
        authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()

        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc = MockMvcBuilders
            .standaloneSetup(RequestController(requestService))
            .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `should return 200 if requests found on GET_ALL`() {
        val pageable = PageRequest.of(0, 10)
        val expectedRequests = requests.map { it.asPreview() }
        val expectedPage: Page<RequestPreview> = PageImpl(expectedRequests, pageable, expectedRequests.size.toLong())

        every { requestService.getUserRequests(
            authenticatedUser = any(),
            userId = any(),
            userName = any(),
            action = any(),
            target = any(),
            requestStatus = any(),
            submittedAt = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Requests.GET_ALL)
        ).andExpectSuccessResponse<Page<RequestPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if requests found with name filter`() {
        val pageable = PageRequest.of(0, 10)
        val expectedRequests = requests.filter { it.action == RequestAction.CREATE }.map { it.asPreview() }
        val expectedPage: Page<RequestPreview> = PageImpl(expectedRequests, pageable, expectedRequests.size.toLong())

        every { requestService.getUserRequests(
            authenticatedUser = any(),
            userId = any(),
            userName = any(),
            action = any(),
            target = any(),
            requestStatus = any(),
            submittedAt = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Requests.GET_ALL).param("action", "CREATE")
        ).andExpectSuccessResponse<Page<RequestPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if requests found with submittedAt filter`() {
        val pageable = PageRequest.of(0, 10)
        val requestSubmittedAt = OffsetDateTime.now().minusDays(2).toString()
        val expectedRequests = requests.filter { it.submittedAt.isEqual(OffsetDateTime.parse(requestSubmittedAt)) }.map { it.asPreview() }
        val expectedPage: Page<RequestPreview> = PageImpl(expectedRequests, pageable, expectedRequests.size.toLong())

        every { requestService.getUserRequests(
            authenticatedUser = any(),
            userId = any(),
            userName = any(),
            action = any(),
            target = any(),
            requestStatus = any(),
            submittedAt = any(),
            page = any(),
            size = any(),
            sortBy = any(),
            sortDirection = any()
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Requests.GET_ALL)
                .param("submittedAt", requestSubmittedAt)
        ).andExpectSuccessResponse<Page<RequestPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 200 if requests found with sort by submittedAt and direction ASC`() {
        val pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "submittedAt"))
        val expectedRequests = requests.sortedBy { it.submittedAt }.map { it.asPreview() }
        val expectedPage: Page<RequestPreview> = PageImpl(expectedRequests, pageable, expectedRequests.size.toLong())

        every { requestService.getUserRequests(
            authenticatedUser = any(),
            userId = any(),
            userName = any(),
            action = any(),
            target = any(),
            requestStatus = any(),
            submittedAt = any(),
            page = any(),
            size = any(),
            sortBy = "submittedAt",
            sortDirection = Sort.Direction.ASC
        ) } returns expectedPage

        mockMvc.perform(
            get(Path.Requests.GET_ALL)
                .param("sortBy", "submittedAt")
                .param("sortDirection", "ASC")
        ).andExpectSuccessResponse<Page<RequestPreview>>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedPage
        )
    }

    @Test
    fun `should return 400 if requestId is invalid on GET`() {
        mockMvc.perform(
            get(Path.Requests.GET, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: requestId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if request not found on GET`() {
        val requestId = UUID.randomUUID()

        every { requestService.getRequest(
            authenticatedUser = any(),
            requestId = any()
        ) } throws ResourceNotFoundException("Request not found")

        mockMvc.perform(
            get(Path.Requests.GET, requestId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Request not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 200 if request found on GET`() {
        val expectedRequest = requests.first()

        every { requestService.getRequest(
            authenticatedUser = any(),
            requestId = any()
        ) } returns expectedRequest.asPublic()

        mockMvc.perform(
            get(Path.Requests.GET, expectedRequest.id)
        ).andExpectSuccessResponse<RequestInformation>(
            expectedStatus = HttpStatus.OK,
            expectedMessage = null,
            expectedData = expectedRequest.asPublic()
        )
    }

    @Test
    fun `should return 200 if request created successfully`() {
        val expectedRequest = requests.first()

        every { requestService.submitRequest(
            authenticatedUser = any(),
            action = any(),
            target = any(),
            extraData = any(),
            justification = any(),
            files = any()
        ) } returns expectedRequest.id

        mockMvc.perform(
            post(Path.Requests.CREATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    RequestCreateInputModel(
                        action = RequestAction.CREATE,
                        target = RequestTarget.CLINIC,
                        extraData = ClinicCreateInputModel(
                            name = "New Clinic",
                            nif = "123456789",
                            address = "123 New Street",
                            lng = 0.0,
                            lat = 0.0,
                            phone = "1234567890",
                            email = "new_clinic@gmail.com",
                            imageUrl = null,
                            ownerId = null
                        ).toJson(),
                        justification = "Justification",
                        files = emptyList()
                ).toJson())
        ).andExpectSuccessResponse<Map<String, UUID>>(
            expectedStatus = HttpStatus.CREATED,
            expectedMessage = null,
            expectedData = mapOf("id" to expectedRequest.id)
        )
    }

    @Test
    fun `should return 400 if requestId is invalid on UPDATE`() {
        mockMvc.perform(
            put(Path.Requests.UPDATE, "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    RequestUpdateInputModel(
                        decision = RequestStatus.APPROVED,
                        justification = "Justification"
                ).toJson())
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: requestId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 200 if request updated successfully`() {
        val expectedRequest = requests.first()

        every { requestService.updateRequest(
            authenticatedUser = any(),
            requestId = any(),
            decision = any(),
            justification = any()
        ) } returns expectedRequest.id

        mockMvc.perform(
            put(Path.Requests.UPDATE, expectedRequest.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(RequestUpdateInputModel(
                    decision = RequestStatus.APPROVED,
                    justification = "Justification"
                ).toJson())
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }

    @Test
    fun `should return 400 if requestId is invalid on DELETE`() {
        mockMvc.perform(
            delete(Path.Requests.DELETE, "invalid")
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.BAD_REQUEST,
            expectedMessage = "Invalid value for path variable: requestId",
            expectedError = "Type mismatch"
        )
    }

    @Test
    fun `should return 404 if request not found on DELETE`() {
        val requestId = requests.first().id
        every { requestService.deleteRequest(
            authenticatedUser = any(),
            requestId = any()
        ) } throws ResourceNotFoundException("Request not found")

        mockMvc.perform(
            delete(Path.Requests.DELETE, requestId)
        ).andExpectErrorResponse(
            expectedStatus = HttpStatus.NOT_FOUND,
            expectedMessage = "Not found: Request not found",
            expectedError = "Resource not found"
        )
    }

    @Test
    fun `should return 204 if request deleted successfully`() {
        val requestId = requests.first().id
        every { requestService.deleteRequest(
            authenticatedUser = any(),
            requestId = requestId
        ) } returns true

        mockMvc.perform(
            delete(Path.Requests.DELETE, requestId)
        ).andExpectSuccessResponse<Void>(
            expectedStatus = HttpStatus.NO_CONTENT,
            expectedMessage = null,
            expectedData = null
        )
    }
}