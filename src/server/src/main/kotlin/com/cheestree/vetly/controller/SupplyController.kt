package com.cheestree.vetly.controller

import com.cheestree.vetly.http.api.SupplyApi
import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.service.SupplyService
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SupplyController(
    private val supplyService: SupplyService,
) : SupplyApi {
    @AuthenticatedRoute
    override fun getAllSupplies(
        name: String?,
        type: SupplyType?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<MedicalSupplyPreview>> =
        ResponseEntity.ok(
            supplyService.getSupplies(
                name = name,
                type = type,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )

    @ProtectedRoute(VETERINARIAN)
    override fun getClinicSupplies(
        authenticatedUser: AuthenticatedUser,
        clinicId: Long,
        name: String?,
        type: String?,
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: Sort.Direction,
    ): ResponseEntity<ResponseList<MedicalSupplyClinicPreview>> =
        ResponseEntity.ok(
            supplyService.getClinicSupplies(
                user = authenticatedUser,
                clinicId = clinicId,
                name = name,
                page = page,
                size = size,
                sortBy = sortBy,
                sortDirection = sortDirection,
            ),
        )

    @AuthenticatedRoute
    override fun getSupply(supplyId: Long): ResponseEntity<MedicalSupplyInformation> =
        ResponseEntity.ok(
            supplyService.getSupply(
                supplyId = supplyId,
            ),
        )

    @ProtectedRoute(VETERINARIAN)
    override fun updateSupply(
        clinicId: Long,
        supplyId: Long,
        supply: MedicalSupplyUpdateInputModel,
    ): ResponseEntity<Void> {
        supplyService.updateSupply(
            clinicId = clinicId,
            supplyId = supplyId,
            quantity = supply.quantity,
            price = supply.price,
        )
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun deleteSupply(
        clinicId: Long,
        supplyId: Long,
    ): ResponseEntity<Void> {
        supplyService.deleteSupply(
            clinicId = clinicId,
            supplyId = supplyId,
        )
        return ResponseEntity.noContent().build()
    }
}
