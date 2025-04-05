package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.RequestMapper
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.repository.RequestRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
class RequestService(
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository,
    private val requestMapper: RequestMapper,
    private val appConfig: AppConfig
) {
    fun getUserRequests(
        authenticatedUser: AuthenticatedUser,
        userId: Long? = null,
        userName: String? = null,
        action: RequestAction? = null,
        target: RequestTarget? = null,
        requestStatus: RequestStatus? = null,
        submittedAt: OffsetDateTime? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "createdAt",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<RequestPreview> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(appConfig.maxPageSize),
            Sort.by(sortDirection, sortBy)
        )

        val finalUserId = if (authenticatedUser.roles.contains(ADMIN)) userId else authenticatedUser.id

        val specs = withFilters<Request>(
            { root, cb -> finalUserId?.let { cb.equal(root.get<Long>("user").get<Long>("id"), it) } },
            { root, cb ->
                if (authenticatedUser.roles.contains(ADMIN) && userName != null) {
                    cb.like(cb.lower(root.get<User>("user").get("name")), "%${userName.lowercase()}%")
                } else null
            },
            { root, cb -> action?.let { cb.equal(root.get<RequestAction>("action"), it) } },
            { root, cb -> target?.let { cb.equal(root.get<RequestTarget>("target"), it) } },
            { root, cb -> requestStatus?.let { cb.equal(root.get<RequestStatus>("status"), it) } },
            { root, cb -> submittedAt?.let { cb.equal(root.get<OffsetDateTime>("submittedAt"), it) } }
        )

        return requestRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID
    ): RequestInformation {
        val request = requestRepository.getRequestById(requestId).orElseThrow {
            throw IllegalArgumentException("Request not found")
        }

        if (request.user.id != authenticatedUser.id && !authenticatedUser.roles.contains(ADMIN)) {
            throw IllegalArgumentException("You are not authorized to view this request")
        }

        return request.asPublic()
    }

    fun submitRequest(
        authenticatedUser: AuthenticatedUser,
        action: RequestAction,
        target: RequestTarget,
        extraData: String?,
        justification: String,
        files: List<String>
    ): UUID {
        requestRepository.getRequestByActionAndTarget(action, target).orElseThrow {
            throw IllegalArgumentException("Request already exists")
        }

        requestMapper.validateOrThrow(extraData, target, action)

        val user = userRepository.findById(authenticatedUser.id).orElseThrow {
            throw IllegalArgumentException("User not found")
        }

        val request = Request(
            user = user,
            action = action,
            target = target,
            justification = justification,
            files = files,
            extraData = extraData,
            submittedAt = OffsetDateTime.now(),
        )

        return requestRepository.save(request).id
    }

    fun updateRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID,
        decision: RequestStatus,
        justification: String,
    ): UUID {
        val request = requestRepository.getRequestById(requestId).orElseThrow {
            throw IllegalArgumentException("Request not found")
        }

        val updatedRequest = request.copy(
            requestStatus = decision,
        )

        return requestRepository.save(updatedRequest).id
    }

    fun deleteRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID
    ): Boolean {
        return requestRepository.deleteRequestById(requestId)
    }
}
