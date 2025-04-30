package com.cheestree.vetly.api

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.http.path.Path.Supplies.DELETE
import com.cheestree.vetly.http.path.Path.Supplies.GET_ALL
import com.cheestree.vetly.http.path.Path.Supplies.GET_CLINIC_SUPPLIES
import com.cheestree.vetly.http.path.Path.Supplies.GET_SUPPLY
import com.cheestree.vetly.http.path.Path.Supplies.UPDATE
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Supply")
interface SupplyApi {
    @Operation(
        summary = "Fetches all medical supplies by filters",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_ALL)
    @AuthenticatedRoute
    fun getAllSupplies(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "type", required = false) type: SupplyType?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<MedicalSupplyPreview>>

    @Operation(
        summary = "Fetches all clinic supplies by filters",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_CLINIC_SUPPLIES)
    @ProtectedRoute(VETERINARIAN)
    fun getClinicSupplies(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @PathVariable clinicId: Long,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "type", required = false) type: String?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<MedicalSupplyClinicPreview>>

    @Operation(
        summary = "Fetches medical supply by ID",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_SUPPLY)
    @AuthenticatedRoute
    fun getSupply(
        @PathVariable supplyId: Long,
    ): ResponseEntity<MedicalSupplyInformation>

    @Operation(
        summary = "Updates medical supply by ID",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PostMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateSupply(
        @PathVariable clinicId: Long,
        @PathVariable supplyId: Long,
        @RequestBody @Valid supply: MedicalSupplyUpdateInputModel,
    ): ResponseEntity<Void>

    @Operation(
        summary = "Deletes medical supply by ID",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteSupply(
        @PathVariable clinicId: Long,
        @PathVariable supplyId: Long,
    ): ResponseEntity<Void>
}
