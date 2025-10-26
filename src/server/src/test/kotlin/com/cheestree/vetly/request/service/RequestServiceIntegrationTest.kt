package com.cheestree.vetly.request.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.TestUtils.daysAgo
import com.cheestree.vetly.TestUtils.daysFromNow
import com.cheestree.vetly.domain.clinic.service.ServiceType
import com.cheestree.vetly.domain.clinic.service.ServiceType.*
import com.cheestree.vetly.domain.exception.VetException.*
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.OpeningHourInputModel
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import com.cheestree.vetly.http.model.input.request.RequestQueryInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.input.user.UserRoleUpdateInputModel
import com.cheestree.vetly.service.RequestService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalTime
import java.util.*

class RequestServiceIntegrationTest : IntegrationTestBase() {
    @Autowired
    private lateinit var requestService: RequestService

    private fun createRequestFrom(
        user: AuthenticatedUser,
        action: RequestAction,
        target: RequestTarget,
        justification: String,
        files: List<String>,
        extraData: RequestExtraData?,
    ): UUID =
        requestService.submitRequest(
            user = user,
            createdRequest =
                RequestCreateInputModel(
                    action = action,
                    target = target,
                    justification = justification,
                    extraData = extraData,
                ),
            files = null,
        )

    private fun validClinicInput(
        name: String = "Valid",
        nif: String = "123456789",
        address: String = "123 Street",
        phone: String = "123456789",
        email: String = "valid@example.com",
        services: Set<ServiceType> = setOf(SURGERY, CHECKUP, DENTISTRY),
        openingHours: List<OpeningHourInputModel> = listOf(
            OpeningHourInputModel(
                0,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
            )
        ),
    ) = ClinicCreateInputModel(
        name = name,
        nif = nif,
        address = address,
        phone = phone,
        lng = 0.0,
        lat = 0.0,
        email = email,
        services = services,
        openingHours = openingHours,
        ownerEmail = null,
    )

    @Nested
    inner class GetAllRequestTests {
        @Test
        fun `should retrieve all requests successfully`() {
            val requests =
                requestService.getRequests(
                    user = savedUsers[0].toAuthenticatedUser(),
                )
            assertThat(requests.elements).hasSize(savedRequests.size)
        }

        @Test
        fun `should filter requests by status`() {
            val requests =
                requestService.getRequests(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query = RequestQueryInputModel(status = RequestStatus.PENDING),
                )

            assertThat(requests.elements).hasSize(2)
            assertThat(requests.elements.first().status).isEqualTo("PENDING")
        }

        @Test
        fun `should filter requests by date`() {
            val requestsInRange =
                requestService.getRequests(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query =
                        RequestQueryInputModel(
                            submittedAfter = daysAgo(2).toLocalDate(),
                            submittedBefore = daysFromNow(2).toLocalDate(),
                        ),
                )

            assertThat(requestsInRange.elements).hasSize(2)
            assertThat(requestsInRange.elements.first().createdAt).isEqualTo(savedRequests[0].createdAt)

            val requestsOutOfRange =
                requestService.getRequests(
                    user = savedUsers[0].toAuthenticatedUser(),
                    query =
                        RequestQueryInputModel(
                            submittedAfter = daysFromNow(3).toLocalDate(),
                            submittedBefore = daysFromNow(5).toLocalDate(),
                        ),
                )

            assertThat(requestsOutOfRange.elements).hasSize(0)
        }
    }

    @Nested
    inner class GetRequestTests {
        @Test
        fun `should retrieve a request by ID successfully`() {
            val request = requestService.getRequest(savedRequests[0].user.toAuthenticatedUser(), savedRequests[0].id)

            assertThat(request).isNotNull
            assertThat(request.id).isEqualTo(savedRequests[0].id)
        }

        @Test
        fun `should throw an exception when request not found`() {
            assertThatThrownBy {
                requestService.getRequest(savedRequests[0].user.toAuthenticatedUser(), nonExistentUUID)
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("Request with id $nonExistentUUID not found")
        }

        @Test
        fun `should throw an exception when user not authorized to view request`() {
            assertThatThrownBy {
                requestService.getRequest(savedUsers[1].toAuthenticatedUser(), savedRequests[0].id)
            }.isInstanceOf(UnauthorizedAccessException::class.java)
                .hasMessageContaining("User is not authorized to view this request")
        }
    }

    @Nested
    inner class CreateRequestTests {
        @Test
        fun `should create a new request successfully`() {
            requestRepository.deleteAll()

            val requestId =
                createRequestFrom(
                    user = savedUsers[0].toAuthenticatedUser(),
                    action = RequestAction.CREATE,
                    target = RequestTarget.CLINIC,
                    justification = "Just because",
                    files = listOf("file1.txt", "file2.txt"),
                    extraData = validClinicInput(),
                )

            assertThat(requestId).isNotNull
            val createdRequest = requestService.getRequest(savedUsers[0].toAuthenticatedUser(), requestId)
            assertThat(createdRequest.id).isEqualTo(requestId)
        }

        @Test
        fun `should throw an exception when creating a duplicate request`() {
            assertThatThrownBy {
                createRequestFrom(
                    user = savedUsers[0].toAuthenticatedUser(),
                    action = RequestAction.CREATE,
                    target = RequestTarget.CLINIC,
                    justification = "Duplicate",
                    files = listOf("file2.txt"),
                    extraData = validClinicInput(),
                )
            }.isInstanceOf(ResourceAlreadyExistsException::class.java)
                .hasMessageContaining("Request with action-target ${RequestAction.CREATE}-${RequestTarget.CLINIC} already exists")
        }

        @Test
        fun `should throw an exception when creating a request with invalid data`() {
            assertThatThrownBy {
                createRequestFrom(
                    user = savedUsers[1].toAuthenticatedUser(),
                    action = RequestAction.UPDATE,
                    target = RequestTarget.ROLE,
                    justification = "Just because",
                    files = listOf("file1.txt", "file2.txt"),
                    extraData = validClinicInput(),
                )
            }.isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("Invalid format for ROLE UPDATE")
        }

        @Test
        fun `should throw an exception when creating a request with invalid action and target`() {
            assertThatThrownBy {
                createRequestFrom(
                    user = savedUsers[0].toAuthenticatedUser(),
                    action = RequestAction.UPDATE,
                    target = RequestTarget.USER,
                    justification = "Just because",
                    extraData = validClinicInput(),
                    files = listOf("file1.txt", "file2.txt"),
                )
            }.isInstanceOf(ValidationException::class.java)
                .hasMessageContaining("Unsupported request target/action combination: USER to UPDATE")
        }
    }

    @Nested
    inner class UpdateRequestTests {
        @Test
        fun `should update a request successfully`() {
            requestService.updateRequest(
                user = savedRequests[0].user.toAuthenticatedUser(),
                requestId = savedRequests[0].id,
                updatedRequest =
                    RequestUpdateInputModel(
                        decision = RequestStatus.APPROVED,
                        justification = "Because I want to",
                    ),
            )

            val retrievedRequest =
                requestService.getRequest(savedRequests[0].user.toAuthenticatedUser(), savedRequests[0].id)
            assertThat(retrievedRequest.status).isEqualTo(RequestStatus.APPROVED)
        }

        @Test
        fun `should update a request successfully and execute action on target`() {
            requestService.updateRequest(
                user = savedRequests[0].user.toAuthenticatedUser(),
                requestId = savedRequests[0].id,
                updatedRequest =
                    RequestUpdateInputModel(
                        decision = RequestStatus.APPROVED,
                        justification = "Because I want to",
                    ),
            )

            val retrievedRequest =
                requestService.getRequest(savedRequests[0].user.toAuthenticatedUser(), savedRequests[0].id)
            assertThat(retrievedRequest.status).isEqualTo(RequestStatus.APPROVED)

            val requestData = retrievedRequest.extraData as Map<String, Any>

            val retrievedClinic =
                clinicRepository.findByLongitudeAndLatitude(
                    lat = (requestData["lat"] as Number).toDouble(),
                    lng = (requestData["lng"] as Number).toDouble(),
                )

            assertThat(retrievedClinic).isNotNull
            assertThat(retrievedClinic.size).isEqualTo(2)

            val clinic = retrievedClinic.first { it.name == requestData["name"] }
            assertThat(clinic.nif).isEqualTo(requestData["nif"])
        }

        @Test
        fun `should update user roles when role update request is approved`() {
            val roleRequest =
                createRequestFrom(
                    user = savedUsers[0].toAuthenticatedUser(),
                    action = RequestAction.UPDATE,
                    target = RequestTarget.ROLE,
                    justification = "Need veterinarian access",
                    files = emptyList(),
                    extraData =
                        UserRoleUpdateInputModel(
                            roleName = Role.VETERINARIAN,
                        ),
                )

            requestService.updateRequest(
                user = savedUsers[0].toAuthenticatedUser(),
                requestId = roleRequest,
                updatedRequest =
                    RequestUpdateInputModel(
                        decision = RequestStatus.APPROVED,
                        justification = "Approved role change",
                    ),
            )

            val updatedUser = userRepository.findById(savedUsers[0].id).get()
            assertThat(updatedUser.roles).anyMatch { it.role.role == Role.VETERINARIAN }
        }

        @Test
        fun `should update request as rejected`() {
            requestService.updateRequest(
                user = savedUsers[0].toAuthenticatedUser(),
                requestId = savedRequests[0].id,
                updatedRequest =
                    RequestUpdateInputModel(
                        decision = RequestStatus.REJECTED,
                        justification = "No, thanks",
                    ),
            )

            val updatedRequest = requestService.getRequest(savedUsers[0].toAuthenticatedUser(), savedRequests[0].id)
            assertThat(updatedRequest.status).isEqualTo(RequestStatus.REJECTED)
        }

        @Test
        fun `should throw an exception when request not found on update`() {
            assertThatThrownBy {
                requestService.updateRequest(
                    user = savedRequests[0].user.toAuthenticatedUser(),
                    requestId = nonExistentUUID,
                    updatedRequest =
                        RequestUpdateInputModel(
                            decision = RequestStatus.APPROVED,
                            justification = "Because I want to",
                        ),
                )
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("Request with id $nonExistentUUID not found")
        }
    }

    @Nested
    inner class DeleteRequestTests {
        @Test
        fun `should delete a request successfully`() {
            assertThat(
                requestService.deleteRequest(
                    savedRequests[0].user.toAuthenticatedUser(),
                    savedRequests[0].id
                )
            ).isTrue
        }

        @Test
        fun `should throw an exception when request not found on delete`() {
            assertThatThrownBy {
                requestService.deleteRequest(savedRequests[0].user.toAuthenticatedUser(), nonExistentUUID)
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("Request with id $nonExistentUUID not found")
        }
    }
}
