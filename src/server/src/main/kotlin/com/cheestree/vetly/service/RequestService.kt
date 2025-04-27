package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.components.RequestExecutor
import com.cheestree.vetly.domain.exception.VetException.*
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.RequestExtraDataTypeRegistry
import com.cheestree.vetly.http.RequestMapper
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.repository.RequestRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class RequestService(
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository,
    private val requestMapper: RequestMapper,
    private val appConfig: AppConfig,
    private val requestExecutor: RequestExecutor
) {
    fun getRequests(
        authenticatedUser: AuthenticatedUser? = null,
        userId: Long? = null,
        userName: String? = null,
        action: RequestAction? = null,
        target: RequestTarget? = null,
        status: RequestStatus? = null,
        submittedAfter: LocalDate? = null,
        submittedBefore: LocalDate? = null,
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

        val isAdmin = authenticatedUser?.roles?.contains(ADMIN) == true

        val resolvedUserId = when {
            isAdmin -> userId
            authenticatedUser != null -> authenticatedUser.id
            else -> userId
        }

        val zoneOffset = OffsetDateTime.now().offset

        val specs = withFilters<Request>(
            { root, cb -> resolvedUserId?.let { cb.equal(root.get<User>("user").get<Long>("id"), it) } },
            { root, cb ->
                if (isAdmin && !userName.isNullOrBlank()) {
                    cb.like(cb.lower(root.get<User>("user").get("name")), "%${userName.lowercase()}%")
                } else null
            },
            { root, cb -> action?.let { cb.equal(root.get<RequestAction>("action"), it) } },
            { root, cb -> target?.let { cb.equal(root.get<RequestTarget>("target"), it) } },
            { root, cb -> status?.let { cb.equal(root.get<RequestStatus>("status"), it) } },
            { root, cb ->
                submittedAfter?.let {
                    cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        it.atStartOfDay().atOffset(zoneOffset).truncatedTo(ChronoUnit.MINUTES)
                    )
                }
            },
            { root, cb ->
                submittedBefore?.let {
                    cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        it.atTime(LocalTime.MAX).atOffset(zoneOffset).truncatedTo(ChronoUnit.MINUTES)
                    )
                }
            },
        )

        return requestRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID
    ): RequestInformation {
        val request = requestRepository.getRequestById(requestId).orElseThrow {
            throw ResourceNotFoundException("Request with id $requestId not found")
        }

        if (request.user.id != authenticatedUser.id && !authenticatedUser.roles.contains(ADMIN)) {
            throw UnauthorizedAccessException("User is not authorized to view this request")
        }

        return request.asPublic()
    }

    fun submitRequest(
        authenticatedUser: AuthenticatedUser,
        action: RequestAction,
        target: RequestTarget,
        extraData: RequestExtraData?,
        justification: String,
        files: List<String>
    ): UUID {
        if(requestRepository.existsRequestByActionAndTargetAndUser_Id(action, target, authenticatedUser.id)){
            throw BadRequestException("Request already exists for this action and target")
        }

        val validatedExtraData = extraData?.let { requestMapper.validateData(it) }

        if (validatedExtraData != null) {
            val expectedType = RequestExtraDataTypeRegistry.expectedTypeFor(target, action)
            if (!expectedType.isInstance(validatedExtraData)) {
                throw BadRequestException("Invalid format for $target $action: expected ${expectedType.simpleName}, got ${validatedExtraData::class.simpleName}")
            }
        }

        val user = userRepository.findById(authenticatedUser.id).orElseThrow {
            throw ResourceNotFoundException("User not found")
        }

        val request = Request(
            user = user,
            action = action,
            target = target,
            justification = justification,
            files = files,
            extraData = requestMapper.requestExtraDataToMap(validatedExtraData)
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
            throw ResourceNotFoundException("Request with id $requestId not found")
        }

        request.updateWith(decision)

        if (decision == RequestStatus.APPROVED) {
            requestExecutor.execute(request)
        }

        return requestRepository.save(request).id
    }

    fun deleteRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID
    ): Boolean {
        val request = requestRepository.getRequestById(requestId).orElseThrow {
            throw ResourceNotFoundException("Request with id $requestId not found")
        }

        requestRepository.delete(request)
        return true
    }
}
