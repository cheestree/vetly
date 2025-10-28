package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.api.RequestApi
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestQueryInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.RequestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.UUID

@RestController
class RequestController(
    private val requestService: RequestService,
) : RequestApi {
    @ProtectedRoute(ADMIN)
    override fun getAllRequests(
        user: AuthenticatedUser,
        query: RequestQueryInputModel,
    ): ResponseEntity<ResponseList<RequestPreview>> = ResponseEntity.ok(requestService.getRequests(user, query))

    @AuthenticatedRoute
    override fun getUserRequests(
        user: AuthenticatedUser,
        query: RequestQueryInputModel,
    ): ResponseEntity<ResponseList<RequestPreview>> = ResponseEntity.ok(requestService.getRequests(user, query))

    @AuthenticatedRoute
    override fun getRequest(
        user: AuthenticatedUser,
        id: UUID,
    ): ResponseEntity<RequestInformation> = ResponseEntity.ok(requestService.getRequest(user, id))

    @AuthenticatedRoute
    override fun createRequest(
        user: AuthenticatedUser,
        createdRequest: RequestCreateInputModel,
        files: List<MultipartFile>?,
    ): ResponseEntity<Map<String, UUID>> {
        val id = requestService.submitRequest(user, createdRequest, files)
        val location = URI.create("${Path.Requests.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(ADMIN)
    override fun updateRequest(
        user: AuthenticatedUser,
        id: UUID,
        updatedRequest: RequestUpdateInputModel,
    ): ResponseEntity<Void> {
        requestService.updateRequest(user, id, updatedRequest)
        return ResponseEntity.noContent().build()
    }

    @AuthenticatedRoute
    override fun deleteRequest(
        user: AuthenticatedUser,
        id: UUID,
    ): ResponseEntity<Void> {
        requestService.deleteRequest(user, id)
        return ResponseEntity.noContent().build()
    }
}
