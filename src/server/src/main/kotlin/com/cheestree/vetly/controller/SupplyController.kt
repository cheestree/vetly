package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.path.Path.Supplies.DELETE
import com.cheestree.vetly.http.path.Path.Supplies.GET_ALL
import com.cheestree.vetly.http.path.Path.Supplies.GET_CLINIC_SUPPLIES
import com.cheestree.vetly.http.path.Path.Supplies.GET_SUPPLY
import com.cheestree.vetly.http.path.Path.Supplies.UPDATE
import com.cheestree.vetly.service.SupplyService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SupplyController(
    private val supplyService: SupplyService
) {
    @GetMapping(GET_ALL)
    fun getAllSupplies(
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "type", required = false) type: String?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<MedicalSupplyInformation>> {
        return ResponseEntity.ok(
            supplyService.getSupplies(
                name = name,
                type = type,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @GetMapping(GET_SUPPLY)
    fun getSupply(
        @PathVariable supplyId: Long
    ): ResponseEntity<MedicalSupplyInformation> {
        return ResponseEntity.ok(
            supplyService.getSupply(
                supplyId = supplyId
            )
        )
    }

    @GetMapping(GET_CLINIC_SUPPLIES)
    @ProtectedRoute(VETERINARIAN)
    fun getClinicSupplies(
        @PathVariable clinicId: Long,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "type", required = false) type: String?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: Sort.Direction
    ): ResponseEntity<Page<MedicalSupplyInformation>> {
        return ResponseEntity.ok(
            supplyService.getSupplies(
                clinicId = clinicId,
                name = name,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection
            )
        )
    }

    @PostMapping(UPDATE)
    @ProtectedRoute(VETERINARIAN)
    fun updateSupply(
        @PathVariable clinicId: Long,
        @PathVariable supplyId: Long,
        @RequestBody @Valid supply: MedicalSupplyUpdateInputModel
    ): ResponseEntity<Void> {
        supplyService.updateSupply(
            clinicId = clinicId,
            supplyId = supplyId,
            quantity = supply.quantity,
            price = supply.price,
        )
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping(DELETE)
    @ProtectedRoute(VETERINARIAN)
    fun deleteSupply(
        @PathVariable clinicId: Long,
        @PathVariable supplyId: Long
    ): ResponseEntity<Void> {
        supplyService.deleteSupply(
            clinicId = clinicId,
            supplyId = supplyId
        )
        return ResponseEntity.noContent().build()
    }
}