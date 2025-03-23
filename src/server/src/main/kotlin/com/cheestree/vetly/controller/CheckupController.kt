package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.enums.Role.MEMBER
import com.cheestree.vetly.domain.enums.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.checkup.CheckupInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.path.Path.Checkups.CREATE
import com.cheestree.vetly.http.path.Path.Checkups.DELETE
import com.cheestree.vetly.http.path.Path.Checkups.GET
import com.cheestree.vetly.http.path.Path.Checkups.GET_ALL
import com.cheestree.vetly.http.path.Path.Checkups.UPDATE
import com.cheestree.vetly.service.CheckupService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime

@RestController
class CheckupController(
    private val checkupService: CheckupService
) {
    @GetMapping(GET_ALL)
    @ProtectedRoute(MEMBER)
    fun getAllCheckups(
        authenticatedUser: AuthenticatedUser,
        @RequestParam(required = false) vetId: Long?,
        @RequestParam(required = false) animalId: Long?,
        @RequestParam(required = false) clinicId: Long?,
        @RequestParam(required = false) dateTimeStart: OffsetDateTime?,
        @RequestParam(required = false) dateTimeEnd: OffsetDateTime?,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int? = 10,
        @RequestParam(required = false) sortBy: String? = "dateTime",
        @RequestParam(required = false) sortDirection: String? = "DESC"
    ): ResponseEntity<Page<Checkup>> {
        return ResponseEntity.ok(
            checkupService.getAllCheckups(
                vetId,
                animalId,
                clinicId,
                dateTimeStart,
                dateTimeEnd,
                page,
                size
            )
        )
    }

    @GetMapping(GET)
    @ProtectedRoute(MEMBER)
    fun getCheckup(
        authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long
    ): ResponseEntity<Checkup> {
        return ResponseEntity.ok(checkupService.getCheckup(checkupId))
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createCheckup(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid checkup: CheckupInputModel
    ): ResponseEntity<Checkup> {
        val createdCheckup = checkupService.createCheckUp(
            ownerId = checkup.ownerId,
            petId = checkup.petId,
            vetId = authenticatedUser.id,
            clinicId = checkup.clinicId,
            time = checkup.time,
            description = checkup.description
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCheckup)
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateCheckup(
        authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long,
        @RequestBody @Valid updatedCheckup: CheckupUpdateInputModel
    ): ResponseEntity<Checkup> {
        val updatedCheckupEntity = checkupService.updateCheckUp(
            vetId = authenticatedUser.id,
            clinicId = updatedCheckup.clinicId,
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
        return ResponseEntity.ok(checkupService.deleteCheckup(authenticatedUser.id, checkupId))
    }
}