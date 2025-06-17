package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.GuideApi
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.GuideService
import java.net.URI
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class GuideController(
    private val guideService: GuideService,
) : GuideApi {
    override fun getAllGuides(
        title: String?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<GuidePreview>> =
        ResponseEntity.ok(
            guideService.getAllGuides(
                title = title,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )

    override fun getGuide(guideId: Long): ResponseEntity<GuideInformation> =
        ResponseEntity.ok(
            guideService.getGuide(
                guideId = guideId,
            ),
        )

    @ProtectedRoute(VETERINARIAN)
    override fun createGuide(
        authenticatedUser: AuthenticatedUser,
        guide: GuideCreateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<Map<String, Long>> {
        val id =
            guideService.createGuide(
                veterinarianId = authenticatedUser.id,
                title = guide.title,
                description = guide.description,
                content = guide.content,
                image = image,
            )
        val location = URI.create("${Path.Guides.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateGuide(
        authenticatedUser: AuthenticatedUser,
        guideId: Long,
        guide: GuideUpdateInputModel,
        image: MultipartFile?,
    ): ResponseEntity<GuideInformation> {
        guideService.updateGuide(
            veterinarianId = authenticatedUser.id,
            roles = authenticatedUser.roles,
            guideId = guideId,
            title = guide.title,
            description = guide.description,
            content = guide.content,
            image = image,
        )
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun deleteGuide(
        authenticatedUser: AuthenticatedUser,
        guideId: Long,
    ): ResponseEntity<Void> {
        guideService.deleteGuide(
            veterinarianId = authenticatedUser.id,
            roles = authenticatedUser.roles,
            guideId = guideId,
        )
        return ResponseEntity.noContent().build()
    }
}
