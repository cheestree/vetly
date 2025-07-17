package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideQueryInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.http.path.Path.Guides.CREATE
import com.cheestree.vetly.http.path.Path.Guides.DELETE
import com.cheestree.vetly.http.path.Path.Guides.GET
import com.cheestree.vetly.http.path.Path.Guides.GET_ALL
import com.cheestree.vetly.http.path.Path.Guides.UPDATE
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

@Tag(name = "Guide")
interface GuideApi {
    @Operation(
        summary = "Fetches all guides by filters",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Guides fetched successfully",
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
    fun getAllGuides(
        @ModelAttribute query: GuideQueryInputModel,
    ): ResponseEntity<ResponseList<GuidePreview>>

    @Operation(
        summary = "Fetches guide by ID",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully fetched guide",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = GuideInformation::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET)
    fun getGuide(
        @PathVariable guideId: Long,
    ): ResponseEntity<GuideInformation>

    @Operation(
        summary = "Creates a new guide",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Successfully created guide",
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
    fun createGuide(
        @HiddenUser user: AuthenticatedUser,
        @RequestPart("guide") @Valid createdGuide: GuideCreateInputModel,
        @RequestPart("image", required = false) image: MultipartFile?,
        @RequestPart("file", required = false) file: MultipartFile?,
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates a guide",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully updated guide",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = GuideInformation::class),
                    ),
                ],
            ),
        ],
    )
    @PatchMapping(UPDATE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateGuide(
        @HiddenUser user: AuthenticatedUser,
        @PathVariable guideId: Long,
        @RequestPart("guide") @Valid updatedGuide: GuideUpdateInputModel,
        @RequestPart("image", required = false) image: MultipartFile?,
        @RequestPart("file", required = false) file: MultipartFile?,
    ): ResponseEntity<GuideInformation>

    @Operation(
        summary = "Deletes a guide",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Successfully deleted guide",
            ),
        ],
    )
    @DeleteMapping(DELETE)
    fun deleteGuide(
        @HiddenUser user: AuthenticatedUser,
        @PathVariable guideId: Long,
    ): ResponseEntity<Void>
}
