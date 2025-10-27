package com.cheestree.vetly.service

import com.cheestree.vetly.config.AppConfig
import com.cheestree.vetly.domain.exception.VetException.*
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.storage.StorageFolder
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.RequestExtraDataTypeRegistry
import com.cheestree.vetly.http.RequestMapper
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestQueryInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.repository.BaseSpecs.combineAll
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.repository.request.RequestRepository
import com.cheestree.vetly.repository.request.RequestSpecs
import com.cheestree.vetly.service.Utils.createResource
import com.cheestree.vetly.service.Utils.deleteResource
import com.cheestree.vetly.service.Utils.retrieveResource
import com.cheestree.vetly.service.Utils.updateResource
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

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

        val specs = combineAll(
            RequestSpecs.byUserId(query.userId, user.roles),
            RequestSpecs.byAction(query.action),
            RequestSpecs.byTarget(query.target),
            RequestSpecs.byStatus(query.status),
            RequestSpecs.byCreatedAt(query.submittedAfter, query.submittedBefore),
            RequestSpecs.byUserName(query.userName, user.roles)
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
        id: UUID,
    ): RequestInformation =
        retrieveResource(ResourceType.REQUEST, id) {
            val request =
                requestRepository.getRequestById(id).orElseThrow {
                    throw ResourceNotFoundException(ResourceType.REQUEST, id)
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
                val expectedType =
                    RequestExtraDataTypeRegistry.expectedTypeFor(createdRequest.target, createdRequest.action)
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

            val savedFiles =
                files?.let { storageService.uploadMultipleFiles(it, StorageFolder.REQUESTS) } ?: emptyList()

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
