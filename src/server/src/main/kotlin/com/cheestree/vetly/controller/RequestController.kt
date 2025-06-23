package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.api.RequestApi
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.RequestService
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDate
import java.util.UUID

@RestController
class RequestController(
    private val requestService: RequestService,
) : RequestApi {
    @ProtectedRoute(ADMIN)
    override fun getAllRequests(
        authenticatedUser: AuthenticatedUser,
        userId: Long?,
        userName: String?,
        action: RequestAction?,
        target: RequestTarget?,
        requestStatus: RequestStatus?,
        submittedBefore: LocalDate?,
        submittedAfter: LocalDate?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<RequestPreview>> =
        ResponseEntity.ok(
            requestService.getRequests(
                authenticatedUser = authenticatedUser,
                userId = userId,
                userName = userName,
                action = action,
                target = target,
                status = requestStatus,
                submittedBefore = submittedBefore,
                submittedAfter = submittedAfter,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )

    @AuthenticatedRoute
    override fun getUserRequests(
        authenticatedUser: AuthenticatedUser,
        action: RequestAction?,
        target: RequestTarget?,
        status: RequestStatus?,
        submittedBefore: LocalDate?,
        submittedAfter: LocalDate?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<RequestPreview>> =
        ResponseEntity.ok(
            requestService.getRequests(
                authenticatedUser = authenticatedUser,
                action = action,
                target = target,
                status = status,
                submittedBefore = submittedBefore,
                submittedAfter = submittedAfter,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )

    @AuthenticatedRoute
    override fun getRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID,
    ): ResponseEntity<RequestInformation> =
        ResponseEntity.ok(
            requestService.getRequest(
                authenticatedUser = authenticatedUser,
                requestId = requestId,
            ),
        )

    @AuthenticatedRoute
    override fun createRequest(
        authenticatedUser: AuthenticatedUser,
        request: RequestCreateInputModel,
    ): ResponseEntity<Map<String, UUID>> {
        val id =
            requestService.submitRequest(
                authenticatedUser = authenticatedUser,
                action = request.action,
                target = request.target,
                extraData = request.extraData,
                justification = request.justification,
                files = request.files,
            )
        val location = URI.create("${Path.Requests.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(ADMIN)
    override fun updateRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID,
        request: RequestUpdateInputModel,
    ): ResponseEntity<Void> {
        requestService.updateRequest(
            authenticatedUser = authenticatedUser,
            requestId = requestId,
            decision = request.decision,
            justification = request.justification,
        )
        return ResponseEntity.noContent().build()
    }

    @AuthenticatedRoute
    override fun deleteRequest(
        authenticatedUser: AuthenticatedUser,
        requestId: UUID,
    ): ResponseEntity<Void> {
        requestService.deleteRequest(
            authenticatedUser = authenticatedUser,
            requestId = requestId,
        )
        return ResponseEntity.noContent().build()
    }
}
