package com.cheestree.vetly.integration.service

import com.cheestree.vetly.IntegrationTestBase
import com.cheestree.vetly.TestUtils.toJson
import com.cheestree.vetly.domain.exception.VetException.BadRequestException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.RequestMapper
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import com.cheestree.vetly.http.model.input.user.UserRoleUpdateInputModel
import com.cheestree.vetly.service.RequestService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class RequestServiceTest : IntegrationTestBase() {

    @Autowired
    private lateinit var requestMapper: RequestMapper

    @Autowired
    private lateinit var requestService: RequestService

    private fun createRequestFrom(
        user: AuthenticatedUser,
        action: RequestAction,
        target: RequestTarget,
        justification: String,
        files: List<String>,
        extraData: RequestExtraData?
    ): UUID {
        return requestService.submitRequest(
            authenticatedUser = user,
            action = action,
            target = target,
            justification = justification,
            files = files,
            extraData = extraData
        )
    }

    private fun validClinicInput(
        name: String = "Valid",
        nif: String = "123456789",
        address: String = "123 Street",
        phone: String = "123456789",
        email: String = "valid@example.com"
    ) = ClinicCreateInputModel(
        name = name,
        nif = nif,
        address = address,
        phone = phone,
        lng = 0.0,
        lat = 0.0,
        email = email,
        imageUrl = null,
        ownerId = null
    )

    @Nested
    inner class GetAllRequestTests {
        @Test
        fun `should retrieve all requests successfully`() {
            val requests = requestService.getRequests()
            assertThat(requests).hasSize(savedRequests.size)
        }

        @Test
        fun `should filter requests by status`() {
            val requests = requestService.getRequests(requestStatus = RequestStatus.PENDING)

            assertThat(requests).hasSize(2)
            assertThat(requests.first().status).isEqualTo("PENDING")
        }

        @Test
        fun `should filter requests by date`() {
            val requests = requestService.getRequests(submittedAt = savedRequests[0].submittedAt)

            assertThat(requests).hasSize(1)
            assertThat(requests.first().submittedAt).isEqualTo(savedRequests[0].submittedAt)
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
                requestService.getRequest(savedRequests[0].user.toAuthenticatedUser(), nonExistentUuid)
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("Request with id $nonExistentUuid not found")
        }
    }

    @Nested
    inner class CreateRequestTests {
        @Test
        fun `should create a new request successfully`() {
            requestRepository.deleteAll()

            val requestId = createRequestFrom(
                user = savedUsers[0].toAuthenticatedUser(),
                action = RequestAction.CREATE,
                target = RequestTarget.CLINIC,
                justification = "Just because",
                files = listOf("file1.txt", "file2.txt"),
                extraData = validClinicInput()
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
                    extraData = validClinicInput()
                )
            }.isInstanceOf(BadRequestException::class.java)
                .hasMessageContaining("Request already exists")
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
                    extraData = validClinicInput()
                )
            }.isInstanceOf(BadRequestException::class.java)
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
                    files = listOf("file1.txt", "file2.txt")
                )
            }.isInstanceOf(BadRequestException::class.java)
                .hasMessageContaining("Unsupported request target/action combination: USER to UPDATE")
        }
    }

    @Nested
    inner class UpdateRequestTests {
        @Test
        fun `should update a request successfully`() {
            requestService.updateRequest(
                authenticatedUser = savedRequests[0].user.toAuthenticatedUser(),
                requestId = savedRequests[0].id,
                decision = RequestStatus.APPROVED,
                justification = "Because I want to"
            )

            val retrievedRequest = requestService.getRequest(savedRequests[0].user.toAuthenticatedUser(), savedRequests[0].id)
            assertThat(retrievedRequest.requestStatus).isEqualTo(RequestStatus.APPROVED)
        }

        @Test
        fun `should throw an exception when request not found on update`() {
            assertThatThrownBy {
                requestService.updateRequest(
                    authenticatedUser = savedRequests[0].user.toAuthenticatedUser(),
                    requestId = nonExistentUuid,
                    decision = RequestStatus.APPROVED,
                    justification = "Because I want to"
                )
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("Request with id $nonExistentUuid not found")
        }
    }

    @Nested
    inner class DeleteRequestTests {
        @Test
        fun `should delete a request successfully`() {
            assertThat(requestService.deleteRequest(savedRequests[0].user.toAuthenticatedUser(), savedRequests[0].id)).isTrue
        }

        @Test
        fun `should throw an exception when request not found on delete`() {
            assertThatThrownBy {
                requestService.deleteRequest(savedRequests[0].user.toAuthenticatedUser(), nonExistentUuid)
            }.isInstanceOf(ResourceNotFoundException::class.java)
                .hasMessageContaining("Request with id $nonExistentUuid not found")
        }
    }
}