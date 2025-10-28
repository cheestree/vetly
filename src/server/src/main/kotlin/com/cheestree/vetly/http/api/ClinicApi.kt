package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicQueryInputModel
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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

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
        ],
    )
    @GetMapping(GET_ALL)
    fun getClinics(
        @ModelAttribute query: ClinicQueryInputModel,
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
        ],
    )
    @GetMapping(GET)
    fun getClinic(
        @PathVariable id: Long,
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
        ],
    )
    @PostMapping(CREATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createClinic(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestPart("clinic") @Valid createdClinic: ClinicCreateInputModel,
        @RequestPart("image", required = false) image: MultipartFile?,
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
        ],
    )
    @PatchMapping(UPDATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateClinic(
        @PathVariable id: Long,
        @RequestPart("clinic") @Valid updatedClinic: ClinicUpdateInputModel,
        @RequestPart("image", required = false) image: MultipartFile?,
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
        ],
    )
    @DeleteMapping(DELETE)
    fun deleteClinic(
        @PathVariable id: Long,
    ): ResponseEntity<Void>
}
