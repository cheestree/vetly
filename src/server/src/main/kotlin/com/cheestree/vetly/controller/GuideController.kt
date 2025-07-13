package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.GuideApi
import com.cheestree.vetly.http.model.input.guide.GuideCreateInputModel
import com.cheestree.vetly.http.model.input.guide.GuideQueryInputModel
import com.cheestree.vetly.http.model.input.guide.GuideUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.guide.GuideInformation
import com.cheestree.vetly.http.model.output.guide.GuidePreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.GuideService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
class GuideController(
    private val guideService: GuideService,
) : GuideApi {
    override fun getAllGuides(query: GuideQueryInputModel): ResponseEntity<ResponseList<GuidePreview>> =
        ResponseEntity.ok(guideService.getAllGuides(query))

    override fun getGuide(guideId: Long): ResponseEntity<GuideInformation> = ResponseEntity.ok(guideService.getGuide(guideId))

    @ProtectedRoute(VETERINARIAN)
    override fun createGuide(
        user: AuthenticatedUser,
        createdGuide: GuideCreateInputModel,
        image: MultipartFile?,
        file: MultipartFile?,
    ): ResponseEntity<Map<String, Long>> {
        val id = guideService.createGuide(user, createdGuide, image, file)
        val location = URI.create("${Path.Guides.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateGuide(
        user: AuthenticatedUser,
        guideId: Long,
        updatedGuide: GuideUpdateInputModel,
        image: MultipartFile?,
        file: MultipartFile?,
    ): ResponseEntity<GuideInformation> {
        guideService.updateGuide(user, guideId, updatedGuide, image, file)
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun deleteGuide(
        user: AuthenticatedUser,
        guideId: Long,
    ): ResponseEntity<Void> {
        guideService.deleteGuide(user, guideId)
        return ResponseEntity.noContent().build()
    }
}
