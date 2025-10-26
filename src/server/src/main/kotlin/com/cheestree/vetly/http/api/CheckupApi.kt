package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupQueryInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path.Checkups.CREATE
import com.cheestree.vetly.http.path.Path.Checkups.DELETE
import com.cheestree.vetly.http.path.Path.Checkups.GET
import com.cheestree.vetly.http.path.Path.Checkups.GET_ALL
import com.cheestree.vetly.http.path.Path.Checkups.UPDATE
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
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Checkup")
interface CheckupApi {
    @Operation(
        summary = "Fetches all checkups by filters",
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
    fun getAllCheckups(
        @HiddenUser user: AuthenticatedUser,
        @ModelAttribute query: CheckupQueryInputModel,
    ): ResponseEntity<ResponseList<CheckupPreview>>

    @Operation(
        summary = "Fetches checkup by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Checkup fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = CheckupInformation::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET)
    fun getCheckup(
        @HiddenUser user: AuthenticatedUser,
        @PathVariable id: Long,
    ): ResponseEntity<CheckupInformation>

    @Operation(
        summary = "Creates a new checkup",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Animal fetched successfully",
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
    fun createCheckup(
        @HiddenUser user: AuthenticatedUser,
        @RequestPart(name = "checkup") @Valid createdCheckup: CheckupCreateInputModel,
        @RequestPart(name = "files", required = false) files: List<MultipartFile> = emptyList(),
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates an existing checkup",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Checkup updated successfully",
            ),
        ],
    )
    @PatchMapping(UPDATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateCheckup(
        @HiddenUser user: AuthenticatedUser,
        @PathVariable id: Long,
        @RequestPart("checkup") @Valid updatedCheckup: CheckupUpdateInputModel,
        @RequestPart("filesToAdd", required = false) filesToAdd: List<MultipartFile>? = null,
        @RequestPart("filesToRemove", required = false) filesToRemove: List<String>? = null,
    ): ResponseEntity<CheckupInformation>

    @Operation(
        summary = "Deletes an existing checkup",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Checkup deleted successfully",
            ),
        ],
    )
    @DeleteMapping(DELETE)
    fun deleteCheckup(
        @HiddenUser user: AuthenticatedUser,
        @PathVariable id: Long,
    ): ResponseEntity<Void>
}
