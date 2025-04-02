package com.cheestree.vetly.controller

import com.cheestree.vetly.converter.Parsers.Companion.parseSortDirection
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.path.Path.Supplies.DELETE
import com.cheestree.vetly.http.path.Path.Supplies.GET_ALL
import com.cheestree.vetly.http.path.Path.Supplies.GET_CLINIC_SUPPLIES
import com.cheestree.vetly.http.path.Path.Supplies.GET_SUPPLY
import com.cheestree.vetly.http.path.Path.Supplies.UPDATE
import com.cheestree.vetly.service.SupplyService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: String
    ): ResponseEntity<Page<MedicalSupplyInformation>> {
        return ResponseEntity.ok(
            supplyService.getAllSupplies(
                name = name,
                type = type,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = parseSortDirection(sortDirection)
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
    fun getClinicSupplies(
        @PathVariable clinicId: Long,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam(name = "sortBy", required = false, defaultValue = "name") sortBy: String,
        @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") sortDirection: String
    ): ResponseEntity<Page<MedicalSupplyInformation>> {
        return ResponseEntity.ok(
            supplyService.getClinicSupplies(
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
    fun updateSupply(
        @PathVariable clinicId: Long,
        @PathVariable supplyId: Long,
        @RequestBody supply: MedicalSupplyUpdateInputModel
    ): ResponseEntity<Void> {
        supplyService.updateSupply(
            clinicId = clinicId,
            supplyId = supplyId,
            quantity = supply.count,
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