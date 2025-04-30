package com.cheestree.vetly.controller

import com.cheestree.vetly.api.CheckupApi
import com.cheestree.vetly.domain.user.AuthenticatedUser
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
import java.net.URI
import java.time.LocalDate

@RestController
class CheckupController(
    private val checkupService: CheckupService,
) : CheckupApi {
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
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<CheckupPreview>> {
        return ResponseEntity.ok(
            checkupService.getAllCheckups(
                veterinarianId = veterinarianId,
                veterinarianName = veterinarianName,
                animalId = animalId,
                animalName = animalName,
                clinicId = clinicId,
                clinicName = clinicName,
                dateTimeStart = dateTimeStart,
                dateTimeEnd = dateTimeEnd,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )
    }

    override fun getCheckup(
        authenticatedUser: AuthenticatedUser,
        checkupId: Long,
    ): ResponseEntity<CheckupInformation> {
        return ResponseEntity.ok(
            checkupService.getCheckup(
                userId = authenticatedUser.id,
                checkupId = checkupId,
            ),
        )
    }

    override fun createCheckup(
        authenticatedUser: AuthenticatedUser,
        checkup: CheckupCreateInputModel,
    ): ResponseEntity<Map<String, Long>> {
        val id =
            checkupService.createCheckUp(
                animalId = checkup.animalId,
                veterinarianId = authenticatedUser.id,
                clinicId = checkup.clinicId,
                time = checkup.dateTime,
                description = checkup.description,
                files = checkup.files,
            )
        val location = URI.create("${Path.Checkups.BASE}/$id")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    override fun updateCheckup(
        authenticatedUser: AuthenticatedUser,
        checkupId: Long,
        checkup: CheckupUpdateInputModel,
    ): ResponseEntity<Void> {
        checkupService.updateCheckUp(
            veterinarianId = authenticatedUser.id,
            checkupId = checkupId,
            dateTime = checkup.dateTime,
            description = checkup.description,
            filesToAdd = checkup.filesToAdd,
            filesToRemove = checkup.filesToRemove,
        )
        return ResponseEntity.noContent().build()
    }

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
