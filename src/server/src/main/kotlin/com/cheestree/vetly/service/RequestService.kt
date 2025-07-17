package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.exception.VetException.ValidationException
import com.cheestree.vetly.domain.filter.Filter
import com.cheestree.vetly.domain.filter.Operation
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.RequestExtraDataTypeRegistry
import com.cheestree.vetly.http.RequestMapper
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestQueryInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.repository.RequestRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.service.Utils.Companion.combineAll
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.deleteResource
import com.cheestree.vetly.service.Utils.Companion.mappedFilters
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import com.cheestree.vetly.service.Utils.Companion.withFilters
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.ZoneOffset
import java.util.UUID

@Service
class RequestService(
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository,
    private val requestMapper: RequestMapper,
    private val requestExecutor: RequestExecutor,
    private val storageService: StorageService,
    private val appConfig: AppConfig,
) {
    fun getRequests(
        user: AuthenticatedUser,
        query: RequestQueryInputModel = RequestQueryInputModel(),
    ): ResponseList<RequestPreview> {
        val pageable =
            PageRequest.of(
                query.page.coerceAtLeast(0),
                query.size.coerceAtMost(appConfig.paging.maxPageSize),
                Sort.by(query.sortDirection, query.sortBy),
            )

        val isAdmin = user.roles.contains(ADMIN)
        val resolvedUserId = if (isAdmin) query.userId else user.id

        val baseFilters =
            mappedFilters<Request>(
                listOf(
                    Filter("user.id", resolvedUserId, Operation.EQUAL),
                    Filter("action", query.action, Operation.EQUAL),
                    Filter("target", query.target, Operation.EQUAL),
                    Filter("status", query.status, Operation.EQUAL),
                    Filter(
                        "createdAt",
                        Pair(
                            query.submittedAfter?.atStartOfDay(ZoneOffset.UTC)?.toOffsetDateTime(),
                            query.submittedBefore?.atStartOfDay(ZoneOffset.UTC)?.toOffsetDateTime(),
                        ),
                        Operation.BETWEEN,
                    ),
                ),
            )

        val extraFilters =
            withFilters<Request>(
                { root, cb ->
                    if (isAdmin && !query.userName.isNullOrBlank()) {
                        cb.like(cb.lower(root.get<User>("user").get("name")), "%${query.userName.lowercase()}%")
                    } else {
                        null
                    }
                },
            )

        val finalSpec = combineAll(baseFilters, extraFilters)

        val pageResult = requestRepository.findAll(finalSpec, pageable).map { it.asPreview() }

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
        user: AuthenticatedUser,
        createdRequest: RequestCreateInputModel,
        files: List<MultipartFile>? = null,
    ): UUID =
        createResource(ResourceType.REQUEST) {
            if (requestRepository.existsRequestByActionAndTargetAndStatusAndUser_Id(
                    createdRequest.action,
                    createdRequest.target,
                    RequestStatus.PENDING,
                    user.id,
                )
            ) {
                throw ResourceAlreadyExistsException(
                    ResourceType.REQUEST,
                    "action-target",
                    "${createdRequest.action.name}-${createdRequest.target.name}",
                )
            }

            val validatedExtraData = createdRequest.extraData?.let { requestMapper.validateData(it) }

            if (validatedExtraData != null) {
                val expectedType = RequestExtraDataTypeRegistry.expectedTypeFor(createdRequest.target, createdRequest.action)
                if (!expectedType.isInstance(validatedExtraData)) {
                    throw ValidationException(
                        "Invalid format for ${createdRequest.target} ${createdRequest.action}: expected ${expectedType.simpleName}, got ${validatedExtraData::class.simpleName}",
                    )
                }
            }

            val user =
                userRepository.findById(user.id).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.USER, user.id)
                }

            val savedFiles = files?.let { storageService.uploadMultipleFiles(it, StorageFolder.REQUESTS) } ?: emptyList()

            val request =
                Request(
                    user = user,
                    action = createdRequest.action,
                    target = createdRequest.target,
                    justification = createdRequest.justification,
                    files = savedFiles,
                    extraData = requestMapper.requestExtraDataToMap(validatedExtraData),
                )

            requestRepository.save(request).id
        }

    fun updateRequest(
        user: AuthenticatedUser,
        requestId: UUID,
        updatedRequest: RequestUpdateInputModel,
    ): UUID =
        updateResource(ResourceType.REQUEST, requestId) {
            val request =
                requestRepository.getRequestById(requestId).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.REQUEST, requestId)
                }

            request.updateWith(updatedRequest.decision)

            if (updatedRequest.decision == RequestStatus.APPROVED) {
                requestExecutor.execute(request)
            }

            requestRepository.save(request).id
        }

    fun deleteRequest(
        user: AuthenticatedUser,
        requestId: UUID,
    ): Boolean =
        deleteResource(ResourceType.REQUEST, requestId) {
            val request =
                requestRepository.getRequestById(requestId).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.REQUEST, requestId)
                }

            if (request.user.id != user.id && !user.roles.contains(ADMIN)) {
                throw UnauthorizedAccessException("User is not authorized to modify this request")
            }

            request.files.forEach { fileUrl ->
                storageService.deleteFile(fileUrl)
            }

            requestRepository.delete(request)
            true
        }
}
