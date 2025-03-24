package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.enums.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.http.path.Path.Guides.DELETE
import com.cheestree.vetly.http.path.Path.Guides.GET
import com.cheestree.vetly.http.path.Path.Guides.GET_ALL
import com.cheestree.vetly.service.GuideService
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
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) page: Int = 0,
        @RequestParam(required = false) size: Int = 10,
        @RequestParam(required = false) sortBy: String = "name",
        @RequestParam(required = false) sortDirection: Sort.Direction = Sort.Direction.DESC
    ): ResponseEntity<Page<GuidePreview>> {
        return ResponseEntity.ok(guideService.getGuides(
            name,
            page,
            size,
            sortBy,
            sortDirection
        ))
    }

    @GetMapping(GET)
    fun getGuide(
        @PathVariable guideId: Long
    ): ResponseEntity<GuidePreview> {
        return ResponseEntity.ok(guideService.getGuide(guideId))
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteGuide(
        authenticatedUser: AuthenticatedUser,
        @PathVariable guideId: Long
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(guideService.deleteGuide(guideId))
    }
}