package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.enums.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.clinic.ClinicInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.path.Path.Clinics.CREATE
import com.cheestree.vetly.http.path.Path.Clinics.DELETE
import com.cheestree.vetly.http.path.Path.Clinics.GET
import com.cheestree.vetly.http.path.Path.Clinics.GET_ALL
import com.cheestree.vetly.http.path.Path.Clinics.UPDATE
import com.cheestree.vetly.service.ClinicService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ClinicController(
    private val clinicService: ClinicService
) {
    @GetMapping(GET_ALL)
    @AuthenticatedRoute
    fun getClinics(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) lat: Double?,
        @RequestParam(required = false) long: Double?,
        @RequestParam(required = false) page: Int = 0,
        @RequestParam(required = false) size: Int = 10,
        @RequestParam(required = false) sortBy: String = "name",
        @RequestParam(required = false) sortDirection: Sort.Direction = Sort.Direction.DESC
    ): ResponseEntity<Page<ClinicPreview>> {
        return ResponseEntity.ok(clinicService.getClinics())
    }

    @GetMapping(GET)
    @AuthenticatedRoute
    fun getClinic(
        @PathVariable clinicId: Long
    ): ResponseEntity<ClinicInformation> {
        return ResponseEntity.ok(clinicService.getClinic(clinicId))
    }

    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createClinic(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid clinic: ClinicInputModel
    ): ResponseEntity<ClinicInformation> {
        return ResponseEntity.ok(clinicService.createClinic(
            clinic.name,
            clinic.nif,
            clinic.address,
            clinic.long,
            clinic.lat,
            clinic.phone,
            clinic.email,
            clinic.imageUrl,
            clinic.ownerId
        ))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateClinic(
        @PathVariable clinicId: Long,
        @RequestBody @Valid updateClinic: ClinicUpdateInputModel
    ): ResponseEntity<ClinicInformation> {
        return ResponseEntity.ok(clinicService.updateClinic(
            clinicId,
            updateClinic.name,
            updateClinic.nif,
            updateClinic.address,
            updateClinic.long,
            updateClinic.lat,
            updateClinic.phone,
            updateClinic.email,
            updateClinic.imageUrl,
            updateClinic.ownerId
        ))
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteClinic(
        @PathVariable clinicId: Long
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(clinicService.deleteClinic(clinicId))
    }
}