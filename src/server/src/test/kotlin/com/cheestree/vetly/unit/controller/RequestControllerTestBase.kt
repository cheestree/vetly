package com.cheestree.vetly.unit.controller

import com.cheestree.vetly.TestUtils.andExpectErrorResponse
import com.cheestree.vetly.TestUtils.andExpectSuccessResponse
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.TestUtils.toJson
import com.cheestree.vetly.UnitTestBase
import com.cheestree.vetly.controller.RequestController
import com.cheestree.vetly.domain.clinic.service.ServiceType.CHECKUP
import com.cheestree.vetly.domain.clinic.service.ServiceType.SURGERY
import com.cheestree.vetly.domain.clinic.service.ServiceType.VACCINATION
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.GlobalExceptionHandler
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.OpeningHourInputModel
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.RequestService
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalTime
import java.time.OffsetDateTime
import java.util.UUID

class RequestControllerTestBase : UnitTestBase() {
    @MockitoBean
    lateinit var userService: UserService

    private val authenticatedUserArgumentResolver = mockk<AuthenticatedUserArgumentResolver>()
    private val user = userWithAdmin.toAuthenticatedUser()
    private var requests = requestsBase
    private var requestService: RequestService = mockk(relaxed = true)
    private val mockMvc: MockMvc

    private val invalidId = "invalid"
    private val missingRequestId = UUID.randomUUID()
    private val validRequestId = requestsBase.first().id
    private val validUserId = userWithVet1.id

    init {
        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns user

        mockMvc =
            MockMvcBuilders
                .standaloneSetup(RequestController(requestService = requestService))
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
    }

    private fun performGetAllRequestsRequest(
        isAdmin: Boolean = false,
        userId: Long? = null,
        params: Map<String, String> = emptyMap(),
    ): ResultActions {
        val path =
            if (isAdmin) {
                Path.Requests.GET_ALL
            } else {
                requireNotNull(userId) { "userId must be provided for user route" }
                Path.Requests.GET_USER_REQUESTS.replace("{userId}", userId.toString())
            }

        val request =
            get(path).apply {
                params.forEach { (key, value) -> param(key, value) }
            }

        return mockMvc.perform(request)
    }

    private fun assertAdminGetAllRequests(
        authenticatedUser: AuthenticatedUser,
        params: Map<String, String> = emptyMap(),
        expectedRequests: List<RequestPreview>,
        page: Int = 0,
        size: Int = 10,
    ) {
        val totalElements = expectedRequests.size.toLong()
        val totalPages = if (totalElements == 0L) 1 else ((totalElements + size - 1) / size).toInt()

        val expectedResponse =
            ResponseList(
                elements = expectedRequests,
                totalElements = totalElements,
                totalPages = totalPages,
                page = page,
                size = size,
            )

        every {
            requestService.getRequests(
                authenticatedUser = authenticatedUser,
                query = any()
            )
        } returns expectedResponse

        performGetAllRequestsRequest(isAdmin = true, params = params)
            .andExpectSuccessResponse(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedResponse,
            )
    }

    private fun assertUserGetAllRequests(
        userId: Long,
        params: Map<String, String> = emptyMap(),
        expectedRequests: List<RequestPreview>,
        page: Int = 0,
        size: Int = 10,
    ) {
        val totalElements = expectedRequests.size.toLong()
        val totalPages = if (totalElements == 0L) 1 else ((totalElements + size - 1) / size).toInt()

        val expectedResponse =
            ResponseList(
                elements = expectedRequests,
                totalElements = totalElements,
                totalPages = totalPages,
                page = page,
                size = size,
            )

        every {
            requestService.getRequests(
                authenticatedUser = any(),
                query = any()
            )
        } returns expectedResponse

        performGetAllRequestsRequest(isAdmin = false, userId = userId, params = params)
            .andExpectSuccessResponse(
                expectedStatus = HttpStatus.OK,
                expectedMessage = null,
                expectedData = expectedResponse,
            )
    }

    @Nested
    inner class GetAllRequestTests {
        @Nested
        inner class AdminTests {
            @Test
            fun `should return 200 if requests found on GET_ALL`() {
                val expected = requests.map { it.asPreview() }
                assertAdminGetAllRequests(
                    authenticatedUser = userWithAdmin.toAuthenticatedUser(),
                    expectedRequests = expected,
                )
            }

            @Test
            fun `should return 200 if requests found with action filter`() {
                val expected =
                    requests
                        .filter { it.action == RequestAction.CREATE }
                        .map { it.asPreview() }

                assertAdminGetAllRequests(
                    authenticatedUser = userWithAdmin.toAuthenticatedUser(),
                    params = mapOf("action" to RequestAction.CREATE.name),
                    expectedRequests = expected,
                )
            }

            @Test
            fun `should return 200 if requests found with submittedAt filter`() {
                val requestSubmittedAt = daysAgo(2).toString()
                val expected =
                    requests
                        .filter { it.createdAt.isEqual(OffsetDateTime.parse(requestSubmittedAt)) }
                        .map { it.asPreview() }

                assertAdminGetAllRequests(
                    authenticatedUser = userWithAdmin.toAuthenticatedUser(),
                    params = mapOf("submittedAt" to requestSubmittedAt),
                    expectedRequests = expected,
                )
            }

            @Test
            fun `should return 200 if requests found with sort by submittedAt and direction ASC`() {
                val expected =
                    requests
                        .sortedBy { it.createdAt }
                        .map { it.asPreview() }

                assertAdminGetAllRequests(
                    authenticatedUser = userWithAdmin.toAuthenticatedUser(),
                    params = mapOf("sortBy" to "submittedAt", "sortDirection" to "ASC"),
                    expectedRequests = expected,
                )
            }
        }

        @Nested
        inner class UserTests {
            @Test
            fun `should return 200 if user requests are found`() {
                val expected = requests.filter { it.user.id == validUserId }.map { it.asPreview() }

                assertUserGetAllRequests(
                    userId = validUserId,
                    expectedRequests = expected,
                )
            }

            @Test
            fun `should return 200 if user requests found with action filter`() {
                val expected =
                    requests
                        .filter { it.action == RequestAction.CREATE && it.user.id == validUserId }
                        .map { it.asPreview() }

                assertUserGetAllRequests(
                    userId = validUserId,
                    params = mapOf("action" to RequestAction.CREATE.name),
                    expectedRequests = expected,
                )
            }

            @Test
            fun `should return 200 if user requests found with submittedAt filter`() {
                val requestSubmittedAt = daysAgo(2).toString()
                val expected =
                    requests
                        .filter { it.createdAt.isEqual(OffsetDateTime.parse(requestSubmittedAt)) && it.user.id == validUserId }
                        .map { it.asPreview() }

                assertUserGetAllRequests(
                    userId = validUserId,
                    params = mapOf("submittedAt" to requestSubmittedAt),
                    expectedRequests = expected,
                )
            }

            @Test
            fun `should return 200 if user requests found with sort by submittedAt and direction ASC`() {
                val expected =
                    requests
                        .filter { it.user.id == validUserId }
                        .sortedBy { it.createdAt }
                        .map { it.asPreview() }

                assertUserGetAllRequests(
                    userId = validUserId,
                    params = mapOf("sortBy" to "submittedAt", "sortDirection" to "ASC"),
                    expectedRequests = expected,
                )
            }
        }
    }

    @Nested
    inner class GetRequestTests {
        @Test
        fun `should return 400 if requestId is invalid on GET`() {
            mockMvc
                .perform(
                    get(Path.Requests.GET, invalidId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("requestId" to "Type mismatch: expected UUID"),
                )
        }

        @Test
        fun `should return 404 if request not found on GET`() {
            every {
                requestService.getRequest(
                    authenticatedUser = any(),
                    requestId = any(),
                )
            } throws ResourceNotFoundException(ResourceType.REQUEST, missingRequestId)

            mockMvc
                .perform(
                    get(Path.Requests.GET, missingRequestId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Request with id $missingRequestId not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 200 if request found on GET`() {
            val expectedRequest = requests.first { it.id == validRequestId }.asPublic()

            every {
                requestService.getRequest(
                    authenticatedUser = any(),
                    requestId = any(),
                )
            } returns expectedRequest

            mockMvc
                .perform(
                    get(Path.Requests.GET, validRequestId),
                ).andExpectSuccessResponse<RequestInformation>(
                    expectedStatus = HttpStatus.OK,
                    expectedMessage = null,
                    expectedData = expectedRequest,
                )
        }
    }

    @Nested
    inner class CreateRequestTests {
        @Test
        fun `should return 200 if request created successfully`() {
            val createdRequest =
                RequestCreateInputModel(
                    action = RequestAction.CREATE,
                    target = RequestTarget.CLINIC,
                    extraData =
                        ClinicCreateInputModel(
                            name = "New Clinic",
                            nif = "123456789",
                            address = "123 New Street",
                            lng = 0.0,
                            lat = 0.0,
                            phone = "1234567890",
                            email = "new_clinic@gmail.com",
                            services = setOf(CHECKUP, VACCINATION, SURGERY),
                            openingHours = listOf(OpeningHourInputModel(1, LocalTime.of(9, 0), LocalTime.of(11, 0))),
                            ownerEmail = null,
                        ),
                    justification = "Justification",
                )

            every {
                requestService.submitRequest(
                    authenticatedUser = any(),
                    action = any(),
                    target = any(),
                    extraData = any(),
                    justification = any(),
                    files = any(),
                )
            } returns validRequestId

            mockMvc
                .perform(
                    post(Path.Requests.CREATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createdRequest.toJson()),
                ).andExpectSuccessResponse<Map<String, UUID>>(
                    expectedStatus = HttpStatus.CREATED,
                    expectedMessage = null,
                    expectedData = mapOf("id" to validRequestId),
                )
        }
    }

    @Nested
    inner class UpdateRequestTests {
        @Test
        fun `should return 400 if requestId is invalid on UPDATE`() {
            val updatedRequest =
                RequestUpdateInputModel(
                    decision = RequestStatus.APPROVED,
                    justification = "Justification",
                )

            mockMvc
                .perform(
                    put(Path.Requests.UPDATE, invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedRequest.toJson()),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("requestId" to "Type mismatch: expected UUID"),
                )
        }

        @Test
        fun `should return 200 if request updated successfully`() {
            val updateRequest =
                RequestUpdateInputModel(
                    decision = RequestStatus.APPROVED,
                    justification = "Justification",
                )

            every {
                requestService.updateRequest(
                    authenticatedUser = any(),
                    requestId = any(),
                    decision = any(),
                    justification = any(),
                )
            } returns validRequestId

            mockMvc
                .perform(
                    put(Path.Requests.UPDATE, validRequestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest.toJson()),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }

    @Nested
    inner class DeleteRequestTests {
        @Test
        fun `should return 400 if requestId is invalid on DELETE`() {
            mockMvc
                .perform(
                    delete(Path.Requests.DELETE, invalidId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedMessage = "Invalid value for path variable",
                    expectedErrorDetails = listOf("requestId" to "Type mismatch: expected UUID"),
                )
        }

        @Test
        fun `should return 404 if request not found on DELETE`() {
            every {
                requestService.deleteRequest(
                    authenticatedUser = any(),
                    requestId = validRequestId,
                )
            } throws ResourceNotFoundException(ResourceType.REQUEST, missingRequestId)

            mockMvc
                .perform(
                    delete(Path.Requests.DELETE, validRequestId),
                ).andExpectErrorResponse(
                    expectedStatus = HttpStatus.NOT_FOUND,
                    expectedMessage = "Not found: Request with id $missingRequestId not found",
                    expectedErrorDetails = listOf(null to "Resource not found"),
                )
        }

        @Test
        fun `should return 204 if request deleted successfully`() {
            every {
                requestService.deleteRequest(
                    authenticatedUser = any(),
                    requestId = validRequestId,
                )
            } returns true

            mockMvc
                .perform(
                    delete(Path.Requests.DELETE, validRequestId),
                ).andExpectSuccessResponse<Void>(
                    expectedStatus = HttpStatus.NO_CONTENT,
                    expectedMessage = null,
                    expectedData = null,
                )
        }
    }
}
