package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
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

@RestController
class GuideController(
    private val guideService: GuideService
) {
    @GetMapping(GET_ALL)
    fun getGuides(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) page: Int = 0,
        @RequestParam(required = false) size: Int = 10,
        @RequestParam(required = false) sortBy: String = "title",
        @RequestParam(required = false) sortDirection: Sort.Direction = Sort.Direction.DESC
    ): ResponseEntity<Page<GuidePreview>> {
        return ResponseEntity.ok(guideService.getGuides(
            title,
            page,
            size,
            sortBy,
            sortDirection
        ))
    }

    @GetMapping(GET)
    fun getGuide(
        @PathVariable guideId: Long
    ): ResponseEntity<GuideInformation> {
        return ResponseEntity.ok(guideService.getGuide(
            guideId
        ))
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createGuide(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid guide: GuideCreateInputModel
    ): ResponseEntity<GuideInformation> {
        return ResponseEntity.ok(guideService.createGuide(
            authenticatedUser.id,
            guide.title,
            guide.description,
            guide.imageUrl,
            guide.text
        ))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateGuide(
        authenticatedUser: AuthenticatedUser,
        @PathVariable guideId: Long,
        @RequestBody @Valid guide: GuideUpdateInputModel
    ): ResponseEntity<GuideInformation> {
        return ResponseEntity.ok(guideService.updateGuide(
            authenticatedUser.id,
            authenticatedUser.roles,
            guideId,
            guide.title,
            guide.description,
            guide.imageUrl,
            guide.text
        ))
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteGuide(
        authenticatedUser: AuthenticatedUser,
        @PathVariable guideId: Long
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(guideService.deleteGuide(
            authenticatedUser.id,
            authenticatedUser.roles,
            guideId
        ))
    }
}