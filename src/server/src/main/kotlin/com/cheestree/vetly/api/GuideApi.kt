package com.cheestree.vetly.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
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

@Tag(name = "Guide")
interface GuideApi {
    @Operation(
        summary = "Fetches all guides by filters",
    )
    @GetMapping(GET_ALL)
    fun getAllGuides(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "title") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<GuidePreview>>

    @Operation(
        summary = "Fetches guide by ID",
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
    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createGuide(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid guide: GuideCreateInputModel,
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates a guide",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateGuide(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable guideId: Long,
        @RequestBody @Valid guide: GuideUpdateInputModel,
    ): ResponseEntity<GuideInformation>

    @Operation(
        summary = "Deletes a guide",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteGuide(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable guideId: Long,
    ): ResponseEntity<Void>
}
