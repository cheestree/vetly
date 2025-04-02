package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.http.path.Path.Guides.CREATE
import com.cheestree.vetly.http.path.Path.Guides.DELETE
import com.cheestree.vetly.http.path.Path.Guides.GET
import com.cheestree.vetly.http.path.Path.Guides.GET_ALL
import com.cheestree.vetly.http.path.Path.Guides.UPDATE
import com.cheestree.vetly.service.GuideService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
class GuideController(
    private val guideService: GuideService
) {
    @GetMapping(GET_ALL)
    fun getAllGuides(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "title") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<GuidePreview>> {
        return ResponseEntity.ok(
            guideService.getAllGuides(
                title = title,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET)
    fun getGuide(
        @PathVariable guideId: Long
    ): ResponseEntity<GuideInformation> {
        return ResponseEntity.ok(guideService.getGuide(
            guideId = guideId
        ))
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createGuide(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid guide: GuideCreateInputModel
    ): ResponseEntity<Map<String, Long>> {
        val id = guideService.createGuide(
            veterinarianId = authenticatedUser.id,
            title = guide.title,
            description = guide.description,
            imageUrl = guide.imageUrl,
            text = guide.text
        )
        val location = URI.create("${Path.Guides.BASE}/${id}")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateGuide(
        authenticatedUser: AuthenticatedUser,
        @PathVariable guideId: Long,
        @RequestBody @Valid guide: GuideUpdateInputModel
    ): ResponseEntity<GuideInformation> {
        guideService.updateGuide(
            veterinarianId = authenticatedUser.id,
            roles = authenticatedUser.roles,
            guideId = guideId,
            title = guide.title,
            description = guide.description,
            imageUrl = guide.imageUrl,
            text = guide.text
        )
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteGuide(
        authenticatedUser: AuthenticatedUser,
        @PathVariable guideId: Long
    ): ResponseEntity<Void> {
        guideService.deleteGuide(
            veterinarianId = authenticatedUser.id,
            roles = authenticatedUser.roles,
            guideId = guideId
        )
        return ResponseEntity.noContent().build()
    }
}