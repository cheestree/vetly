package com.cheestree.vetly.api

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.checkup.CheckupCreateInputModel
import com.cheestree.vetly.http.model.input.checkup.CheckupUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.checkup.CheckupInformation
import com.cheestree.vetly.http.model.output.checkup.CheckupPreview
import com.cheestree.vetly.http.path.Path.Checkups.CREATE
import com.cheestree.vetly.http.path.Path.Checkups.DELETE
import com.cheestree.vetly.http.path.Path.Checkups.GET
import com.cheestree.vetly.http.path.Path.Checkups.GET_ALL
import com.cheestree.vetly.http.path.Path.Checkups.UPDATE
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
import java.time.LocalDate

@Tag(name = "Checkup")
interface CheckupApi {
    @Operation(
        summary = "Fetches all checkups by filters",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_ALL)
    @AuthenticatedRoute
    fun getAllCheckups(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestParam(name = "veterinarianId", required = false) veterinarianId: Long?,
        @RequestParam(name = "veterinarianName", required = false) veterinarianName: String?,
        @RequestParam(name = "animalId", required = false) animalId: Long?,
        @RequestParam(name = "animalName", required = false) animalName: String?,
        @RequestParam(name = "clinicId", required = false) clinicId: Long?,
        @RequestParam(name = "clinicName", required = false) clinicName: String?,
        @RequestParam(name = "dateTimeStart", required = false) dateTimeStart: LocalDate?,
        @RequestParam(name = "dateTimeEnd", required = false) dateTimeEnd: LocalDate?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "dateTimeStart") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<CheckupPreview>>

    @Operation(
        summary = "Fetches checkup by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET)
    @AuthenticatedRoute
    fun getCheckup(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long,
    ): ResponseEntity<CheckupInformation>

    @Operation(
        summary = "Creates a new checkup",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping(CREATE)
    @ProtectedRoute(VETERINARIAN)
    fun createCheckup(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid checkup: CheckupCreateInputModel,
    ): ResponseEntity<Map<String, Long>>

    @Operation(
        summary = "Updates an existing checkup",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateCheckup(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long,
        @RequestBody @Valid checkup: CheckupUpdateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes an existing checkup",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteCheckup(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable checkupId: Long,
    ): ResponseEntity<Void>
}
