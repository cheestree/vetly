package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.CheckupApi
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.CheckupService
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.time.LocalDate

@RestController
class CheckupController(
    private val checkupService: CheckupService,
) : CheckupApi {
    @AuthenticatedRoute
    override fun getAllCheckups(
        authenticatedUser: AuthenticatedUser,
        veterinarianId: Long?,
        veterinarianName: String?,
        animalId: Long?,
        animalName: String?,
        clinicId: Long?,
        clinicName: String?,
        dateTimeStart: LocalDate?,
        dateTimeEnd: LocalDate?,
        title: String?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<CheckupPreview>> =
        ResponseEntity.ok(
            checkupService.getAllCheckups(
                authenticatedUser = authenticatedUser,
                veterinarianId = veterinarianId,
                veterinarianName = veterinarianName,
                animalId = animalId,
                animalName = animalName,
                clinicId = clinicId,
                clinicName = clinicName,
                dateTimeStart = dateTimeStart,
                dateTimeEnd = dateTimeEnd,
                title = title,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )

    @AuthenticatedRoute
    override fun getCheckup(
        authenticatedUser: AuthenticatedUser,
        checkupId: Long,
    ): ResponseEntity<CheckupInformation> =
        ResponseEntity.ok(
            checkupService.getCheckup(
                user = authenticatedUser,
                checkupId = checkupId,
            ),
        )

    @ProtectedRoute(VETERINARIAN)
    override fun createCheckup(
        authenticatedUser: AuthenticatedUser,
        checkup: CheckupCreateInputModel,
        files: List<MultipartFile>,
    ): ResponseEntity<Map<String, Long>> {
        val id =
            checkupService.createCheckUp(
                animalId = checkup.animalId,
                veterinarianId = checkup.veterinarianId ?: authenticatedUser.publicId,
                clinicId = checkup.clinicId,
                time = checkup.dateTime,
                title = checkup.title,
                description = checkup.description,
                files = files,
            )
        val location = URI.create("${Path.Checkups.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateCheckup(
        authenticatedUser: AuthenticatedUser,
        checkupId: Long,
        checkup: CheckupUpdateInputModel,
        filesToAdd: List<MultipartFile>?,
        filesToRemove: List<String>?,
    ): ResponseEntity<Void> {
        checkupService.updateCheckUp(
            veterinarianId = authenticatedUser.id,
            checkupId = checkupId,
            dateTime = checkup.dateTime,
            title = checkup.title,
            description = checkup.description,
            filesToAdd = filesToAdd,
            filesToRemove = filesToRemove,
        )
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun deleteCheckup(
        authenticatedUser: AuthenticatedUser,
        checkupId: Long,
    ): ResponseEntity<Void> {
        checkupService.deleteCheckup(
            role = authenticatedUser.roles,
            veterinarianId = authenticatedUser.id,
            checkupId = checkupId,
        )
        return ResponseEntity.noContent().build()
    }
}
