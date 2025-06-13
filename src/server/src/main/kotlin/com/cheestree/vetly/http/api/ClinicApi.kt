package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.error.ApiError
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.path.Path.Clinics.CREATE
import com.cheestree.vetly.http.path.Path.Clinics.DELETE
import com.cheestree.vetly.http.path.Path.Clinics.GET
import com.cheestree.vetly.http.path.Path.Clinics.GET_ALL
import com.cheestree.vetly.http.path.Path.Clinics.UPDATE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Clinic")
interface ClinicApi {
    @Operation(
        summary = "Fetches all clinics by filters",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Checkups fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ResponseList::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_ALL)
    fun getClinics(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "lat", required = false) lat: Double?,
        @RequestParam(name = "lng", required = false) lng: Double?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<ClinicPreview>>

    @Operation(
        summary = "Fetches clinic by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Clinic fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ClinicInformation::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET)
    fun getClinic(
        @PathVariable clinicId: Long,
    ): ResponseEntity<ClinicInformation>

    @Operation(
        summary = "Creates a clinic",
        description = "Requires admin role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Clinic created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Map::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(CREATE)
    fun createClinic(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid clinic: ClinicCreateInputModel,
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates a clinic",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Clinic updated successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @PutMapping(UPDATE)
    fun updateClinic(
        @PathVariable clinicId: Long,
        @RequestBody @Valid updateClinic: ClinicUpdateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes a clinic",
        description = "Requires admin role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Clinic updated successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @DeleteMapping(DELETE)
    fun deleteClinic(
        @PathVariable clinicId: Long,
    ): ResponseEntity<Void>
}
