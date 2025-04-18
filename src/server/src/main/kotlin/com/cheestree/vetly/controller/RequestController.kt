package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.http.path.Path.Requests.CREATE
import com.cheestree.vetly.http.path.Path.Requests.DELETE
import com.cheestree.vetly.http.path.Path.Requests.GET
import com.cheestree.vetly.http.path.Path.Requests.GET_ALL
import com.cheestree.vetly.http.path.Path.Requests.GET_USER_REQUESTS
import com.cheestree.vetly.http.path.Path.Requests.UPDATE
import com.cheestree.vetly.service.RequestService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.time.OffsetDateTime
import java.util.*

@RestController
class RequestController(
    private val requestService: RequestService
){
    @GetMapping(GET_ALL)
    @ProtectedRoute(ADMIN)
    fun getAllRequests(
        authenticatedUser: AuthenticatedUser,
        @RequestParam(name = "userId", required = false) userId: Long?,
        @RequestParam(name = "userName", required = false) userName: String?,
        @RequestParam(name = "action", required = false) action: RequestAction?,
        @RequestParam(name = "target", required = false) target: RequestTarget?,
        @RequestParam(name = "requestStatus", required = false) requestStatus: RequestStatus?,
        @RequestParam(name = "submittedAt", required = false) submittedAt: OffsetDateTime?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "submittedAt") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<RequestPreview>> {
        return ResponseEntity.ok(
            requestService.getRequests(
                authenticatedUser = authenticatedUser,
                userId = userId,
                userName = userName,
                action = action,
                target = target,
                requestStatus = requestStatus,
                submittedAt = submittedAt,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET_USER_REQUESTS)
    @AuthenticatedRoute
    fun getUserRequests(
        @RequestParam(name = "action", required = false) action: RequestAction?,
        @RequestParam(name = "target", required = false) target: RequestTarget?,
        @RequestParam(name = "requestStatus", required = false) requestStatus: RequestStatus?,
        @RequestParam(name = "submittedAt", required = false) submittedAt: OffsetDateTime?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "submittedAt") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<RequestPreview>> {
        return ResponseEntity.ok(
            requestService.getRequests(
                action = action,
                target = target,
                requestStatus = requestStatus,
                submittedAt = submittedAt,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET)
    @AuthenticatedRoute
    fun getRequest(
        authenticatedUser: AuthenticatedUser,
        @PathVariable @Valid requestId: UUID
    ): ResponseEntity<RequestInformation> {
        return ResponseEntity.ok(
            requestService.getRequest(
                authenticatedUser = authenticatedUser,
                requestId = requestId
            )
        )
    }

    @PostMapping(CREATE)
    @AuthenticatedRoute
    fun createRequest(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid request: RequestCreateInputModel
    ): ResponseEntity<Map<String, UUID>> {
        val id = requestService.submitRequest(
            authenticatedUser = authenticatedUser,
            action = request.action,
            target = request.target,
            extraData = request.extraData,
            justification = request.justification,
            files = request.files
        )
        val location = URI.create("${Path.Requests.BASE}/${id}")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(ADMIN)
    fun updateRequest(
        authenticatedUser: AuthenticatedUser,
        @PathVariable requestId: UUID,
        @RequestBody @Valid request: RequestUpdateInputModel
    ): ResponseEntity<Void> {
        requestService.updateRequest(
            authenticatedUser = authenticatedUser,
            requestId = requestId,
            decision = request.decision,
            justification = request.justification,
        )
        return ResponseEntity.noContent().build()

    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(ADMIN)
    fun deleteRequest(
        authenticatedUser: AuthenticatedUser,
        @PathVariable @Valid requestId: UUID
    ): ResponseEntity<Void> {
        requestService.deleteRequest(
            authenticatedUser = authenticatedUser,
            requestId = requestId
        )
        return ResponseEntity.noContent().build()
    }
}