package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.exception.VetException.ValidationException
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.RequestExtraDataTypeRegistry
import com.cheestree.vetly.http.RequestMapper
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.repository.RequestRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import com.cheestree.vetly.service.Utils.Companion.withFilters
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class RequestService(
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository,
    private val requestMapper: RequestMapper,
    private val requestExecutor: RequestExecutor,
    private val firebaseStorageService: FirebaseStorageService,
    private val appConfig: AppConfig,
) {
    fun getRequests(
        authenticatedUser: AuthenticatedUser,
        userId: Long? = null,
        userName: String? = null,
        action: RequestAction? = null,
        target: RequestTarget? = null,
        status: RequestStatus? = null,
        submittedAfter: LocalDate? = null,
        submittedBefore: LocalDate? = null,
        page: Int = 0,
        size: Int = appConfig.paging.defaultPageSize,
        sortBy: String = "createdAt",
        sortDirection: Sort.Direction = Sort.Direction.DESC,
    ): ResponseList<RequestPreview> {
        val pageable =
            PageRequest.of(
                page.coerceAtLeast(0),
                size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(sortDirection, sortBy),
            )

        val isAdmin = authenticatedUser.roles.contains(ADMIN)
        val resolvedUserId = if (isAdmin) userId else authenticatedUser.id

        val zoneOffset = OffsetDateTime.now().offset

        val specs =
            withFilters<Request>(
                { root, cb -> resolvedUserId?.let { cb.equal(root.get<User>("user").get<Long>("id"), it) } },
                { root, cb ->
                    if (isAdmin && !userName.isNullOrBlank()) {
                        cb.like(cb.lower(root.get<User>("user").get("name")), "%${userName.lowercase()}%")
                    } else {
                        null
                    }
                },
                { root, cb -> action?.let { cb.equal(root.get<RequestAction>("action"), it) } },
                { root, cb -> target?.let { cb.equal(root.get<RequestTarget>("target"), it) } },
                { root, cb -> status?.let { cb.equal(root.get<RequestStatus>("status"), it) } },
                { root, cb ->
                    submittedAfter?.let {
                        cb.greaterThanOrEqualTo(
                            root.get("createdAt"),
                            it.atStartOfDay().atOffset(zoneOffset).truncatedTo(ChronoUnit.MINUTES),
                        )
                    }
                },
                { root, cb ->
                    submittedBefore?.let {
                        cb.lessThanOrEqualTo(
                            root.get("createdAt"),
                            it.atTime(LocalTime.MAX).atOffset(zoneOffset).truncatedTo(ChronoUnit.MINUTES),
                        )
                    }
                },
            )

        val pageResult = requestRepository.findAll(specs, pageable).map { it.asPreview() }

        return ResponseList(
            elements = pageResult.content,
            page = pageResult.number,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
        )
    }

    fun getRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID,
    ): RequestInformation =
        retrieveResource(ResourceType.REQUEST, requestId) {
            val request =
                requestRepository.getRequestById(requestId).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.REQUEST, requestId)
                }

            if (request.user.id != authenticatedUser.id && !authenticatedUser.roles.contains(ADMIN)) {
                throw UnauthorizedAccessException("User is not authorized to view this request")
            }

            request.asPublic()
        }

    fun submitRequest(
        authenticatedUser: AuthenticatedUser,
        action: RequestAction,
        target: RequestTarget,
        extraData: RequestExtraData?,
        justification: String,
        files: List<MultipartFile>? = null,
    ): UUID =
        createResource(ResourceType.REQUEST) {
            if (requestRepository.existsRequestByActionAndTargetAndStatusAndUser_Id(action, target, RequestStatus.PENDING, authenticatedUser.id)) {
                throw ResourceAlreadyExistsException(ResourceType.REQUEST, "action-target", "${action.name}-${target.name}")
            }

            val validatedExtraData = extraData?.let { requestMapper.validateData(it) }

            if (validatedExtraData != null) {
                val expectedType = RequestExtraDataTypeRegistry.expectedTypeFor(target, action)
                if (!expectedType.isInstance(validatedExtraData)) {
                    throw ValidationException(
                        "Invalid format for $target $action: expected ${expectedType.simpleName}, got ${validatedExtraData::class.simpleName}",
                    )
                }
            }

            val user =
                userRepository.findById(authenticatedUser.id).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.USER, authenticatedUser.id)
                }

            val savedFiles = files?.let { firebaseStorageService.uploadMultipleFiles(it, StorageFolder.REQUESTS) } ?: emptyList()

            val request =
                Request(
                    user = user,
                    action = action,
                    target = target,
                    justification = justification,
                    files = savedFiles,
                    extraData = requestMapper.requestExtraDataToMap(validatedExtraData),
                )

            requestRepository.save(request).id
        }

    fun updateRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID,
        decision: RequestStatus,
        justification: String,
    ): UUID =
        updateResource(ResourceType.REQUEST, requestId) {
            val request =
                requestRepository.getRequestById(requestId).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.REQUEST, requestId)
                }

            request.updateWith(decision)

            if (decision == RequestStatus.APPROVED) {
                requestExecutor.execute(request)
            }

            requestRepository.save(request).id
        }

    fun deleteRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID,
    ): Boolean =
        deleteResource(ResourceType.REQUEST, requestId) {
            val request =
                requestRepository.getRequestById(requestId).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.REQUEST, requestId)
                }

            if (request.user.id != authenticatedUser.id && !authenticatedUser.roles.contains(ADMIN)) {
                throw UnauthorizedAccessException("User is not authorized to modify this request")
            }

            request.files.forEach { fileUrl ->
                firebaseStorageService.deleteFile(fileUrl)
            }

            requestRepository.delete(request)
            true
        }
}
