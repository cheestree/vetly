package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.CheckupApi
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupQueryInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.CheckupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@RestController
class CheckupController(
    private val checkupService: CheckupService,
) : CheckupApi {
    @AuthenticatedRoute
    override fun getAllCheckups(
        user: AuthenticatedUser,
        query: CheckupQueryInputModel,
    ): ResponseEntity<ResponseList<CheckupPreview>> = ResponseEntity.ok(checkupService.getAllCheckups(user, query))

    @AuthenticatedRoute
    override fun getCheckup(
        user: AuthenticatedUser,
        checkupId: Long,
    ): ResponseEntity<CheckupInformation> = ResponseEntity.ok(checkupService.getCheckup(user, checkupId))

    @ProtectedRoute(VETERINARIAN)
    override fun createCheckup(
        user: AuthenticatedUser,
        createdCheckup: CheckupCreateInputModel,
        files: List<MultipartFile>,
    ): ResponseEntity<Map<String, Long>> {
        val id = checkupService.createCheckUp(user, createdCheckup, files)
        val location = URI.create("${Path.Checkups.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateCheckup(
        user: AuthenticatedUser,
        checkupId: Long,
        updatedCheckup: CheckupUpdateInputModel,
        filesToAdd: List<MultipartFile>?,
        filesToRemove: List<String>?,
    ): ResponseEntity<CheckupInformation> {
        checkupService.updateCheckUp(user, checkupId, updatedCheckup, filesToAdd, filesToRemove)
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun deleteCheckup(
        user: AuthenticatedUser,
        checkupId: Long,
    ): ResponseEntity<Void> {
        checkupService.deleteCheckup(user, checkupId)
        return ResponseEntity.noContent().build()
    }
}
