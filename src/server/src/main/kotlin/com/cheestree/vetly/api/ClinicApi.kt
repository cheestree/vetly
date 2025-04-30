package com.cheestree.vetly.api

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.path.Path.Clinics.CREATE
import com.cheestree.vetly.http.path.Path.Clinics.DELETE
import com.cheestree.vetly.http.path.Path.Clinics.GET
import com.cheestree.vetly.http.path.Path.Clinics.GET_ALL
import com.cheestree.vetly.http.path.Path.Clinics.UPDATE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Clinic")
interface ClinicApi {
    @Operation(
        summary = "Fetches all clinics by filters",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_ALL)
    @AuthenticatedRoute
    fun getClinics(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "lat", required = false) lat: Double?,
        @RequestParam(name = "lng", required = false) lng: Double?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<ClinicPreview>>

    @Operation(
        summary = "Fetches clinic by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET)
    @AuthenticatedRoute
    fun getClinic(
        @PathVariable clinicId: Long,
    ): ResponseEntity<ClinicInformation>

    @Operation(
        summary = "Creates a clinic",
        description = "Requires admin role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping(CREATE)
    @ProtectedRoute(ADMIN)
    fun createClinic(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid clinic: ClinicCreateInputModel,
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates a clinic",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateClinic(
        @PathVariable clinicId: Long,
        @RequestBody @Valid updateClinic: ClinicUpdateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes a clinic",
        description = "Requires admin role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping(DELETE)
    @ProtectedRoute(ADMIN)
    fun deleteClinic(
        @PathVariable clinicId: Long,
    ): ResponseEntity<Void>
}
