package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path.Checkups.CREATE
import com.cheestree.vetly.http.path.Path.Checkups.DELETE
import com.cheestree.vetly.http.path.Path.Checkups.GET
import com.cheestree.vetly.http.path.Path.Checkups.GET_ALL
import com.cheestree.vetly.http.path.Path.Checkups.UPDATE
import com.cheestree.vetly.service.CheckupService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime

@RestController
class CheckupController(
    private val checkupService: CheckupService
) {
    @GetMapping(GET_ALL)
    @AuthenticatedRoute
    fun getAllCheckups(
        authenticatedUser: AuthenticatedUser,
        @RequestParam(name = "veterinarianId", required = false) veterinarianId: Long?,
        @RequestParam(name = "animalId", required = false) animalId: Long?,
        @RequestParam(name = "clinicId", required = false) clinicId: Long?,
        @RequestParam(name = "dateTimeStart", required = false) dateTimeStart: OffsetDateTime?,
        @RequestParam(name = "dateTimeEnd", required = false) dateTimeEnd: OffsetDateTime?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String = "dateTime",
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<CheckupPreview>> {
        return ResponseEntity.ok(
            checkupService.getAllCheckups(
                veterinarianId = veterinarianId,
                animalId = animalId,
                clinicId = clinicId,
                dateTimeStart = dateTimeStart,
                dateTimeEnd = dateTimeEnd,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET)
    @AuthenticatedRoute
    fun getCheckup(
        authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long
    ): ResponseEntity<CheckupInformation> {
        return ResponseEntity.ok(checkupService.getCheckup(
            userId = authenticatedUser.id,
            checkupId = checkupId
        ))
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createCheckup(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid checkup: CheckupCreateInputModel
    ): ResponseEntity<CheckupInformation> {
        val createdCheckup = checkupService.createCheckUp(
            ownerId = checkup.ownerId,
            petId = checkup.petId,
            vetId = authenticatedUser.id,
            clinicId = checkup.clinicId,
            time = checkup.time,
            description = checkup.description,
            files = checkup.files
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCheckup)
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateCheckup(
        authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long,
        @RequestBody @Valid updatedCheckup: CheckupUpdateInputModel
    ): ResponseEntity<CheckupInformation> {
        val updatedCheckupEntity = checkupService.updateCheckUp(
            veterinarianId = authenticatedUser.id,
            checkupId = checkupId,
            updatedVetId = updatedCheckup.vetId,
            updatedTime = updatedCheckup.time,
            updatedDescription = updatedCheckup.description
        )
        return ResponseEntity.ok(updatedCheckupEntity)
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteCheckup(
        authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long,
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(checkupService.deleteCheckup(
            role = authenticatedUser.roles.firstOrNull { it == ADMIN },
            veterinarianId = authenticatedUser.id,
            checkupId = checkupId
        ))
    }
}