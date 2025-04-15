package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.path.Path
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
import java.net.URI

@RestController
class ClinicController(
    private val clinicService: ClinicService
) {
    @GetMapping(GET_ALL)
    @AuthenticatedRoute
    fun getClinics(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "lat", required = false) lat: Double?,
        @RequestParam(name = "lng", required = false) lng: Double?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<ClinicPreview>> {
        return ResponseEntity.ok(
            clinicService.getAllClinics(
                name = name,
                lat = lat,
                lng = lng,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET)
    @AuthenticatedRoute
    fun getClinic(
        @PathVariable clinicId: Long
    ): ResponseEntity<ClinicInformation> {
        return ResponseEntity.ok(
            clinicService.getClinic(
                clinicId = clinicId
            )
        )
    }

    @PostMapping(CREATE)
    @ProtectedRoute(ADMIN)
    fun createClinic(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid clinic: ClinicCreateInputModel
    ): ResponseEntity<Map<String, Long>> {
        val id = clinicService.createClinic(
            name = clinic.name,
            nif = clinic.nif,
            address = clinic.address,
            lng = clinic.lng,
            lat = clinic.lat,
            phone = clinic.phone,
            email = clinic.email,
            imageUrl = clinic.imageUrl,
            ownerId = authenticatedUser.id
        )
        val location = URI.create("${Path.Clinics.BASE}/${id}")

        return ResponseEntity.created(location).body(mapOf("id" to id))
    }

    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateClinic(
        @PathVariable clinicId: Long,
        @RequestBody @Valid updateClinic: ClinicUpdateInputModel
    ): ResponseEntity<Void> {
        clinicService.updateClinic(
            clinicId = clinicId,
            name = updateClinic.name,
            nif = updateClinic.nif,
            address = updateClinic.address,
            lng = updateClinic.lng,
            lat = updateClinic.lat,
            phone = updateClinic.phone,
            email = updateClinic.email,
            imageUrl = updateClinic.imageUrl
        )
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(ADMIN)
    fun deleteClinic(
        @PathVariable clinicId: Long
    ): ResponseEntity<Void> {
        clinicService.deleteClinic(clinicId)
        return ResponseEntity.noContent().build()
    }
}