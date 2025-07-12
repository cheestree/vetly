package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.error.ApiError
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.checkup.CheckupQueryInputModel
import com.cheestree.vetly.http.model.input.request.RequestCreateInputModel
import com.cheestree.vetly.http.model.input.request.RequestQueryInputModel
import com.cheestree.vetly.http.model.input.request.RequestUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import com.cheestree.vetly.http.path.Path.Requests.CREATE
import com.cheestree.vetly.http.path.Path.Requests.DELETE
import com.cheestree.vetly.http.path.Path.Requests.GET
import com.cheestree.vetly.http.path.Path.Requests.GET_ALL
import com.cheestree.vetly.http.path.Path.Requests.GET_USER_REQUESTS
import com.cheestree.vetly.http.path.Path.Requests.UPDATE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.UUID

@Tag(name = "Request")
interface RequestApi {
    @Operation(
        summary = "Fetches all requests by filters",
        description = "Requires admin role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Requests fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ResponseList::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_ALL)
    fun getAllRequests(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @ModelAttribute query: RequestQueryInputModel
    ): ResponseEntity<ResponseList<RequestPreview>>

    @Operation(
        summary = "Fetches users' requests by filters",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully fetched requests",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ResponseList::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_USER_REQUESTS)
    fun getUserRequests(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @ModelAttribute query: RequestQueryInputModel
    ): ResponseEntity<ResponseList<RequestPreview>>

    @Operation(
        summary = "Fetches request by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully fetched request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = RequestInformation::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET)
    fun getRequest(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable @Valid requestId: UUID,
    ): ResponseEntity<RequestInformation>

    @Operation(
        summary = "Creates a new request",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully created request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Map::class),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(CREATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createRequest(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestPart("request") @Valid request: RequestCreateInputModel,
        @RequestPart("files", required = false) files: List<MultipartFile>?,
    ): ResponseEntity<Map<String, UUID>>

    @Operation(
        summary = "Updates existing request",
        description = "Requires admin role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully updated request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Map::class),
                    ),
                ],
            ),
        ],
    )
    @PutMapping(UPDATE)
    fun updateRequest(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable requestId: UUID,
        @RequestBody @Valid request: RequestUpdateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes request",
        description = "Requires admin role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully deleted request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Map::class),
                    ),
                ],
            ),
        ],
    )
    @DeleteMapping(DELETE)
    fun deleteRequest(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable @Valid requestId: UUID,
    ): ResponseEntity<Void>
}
