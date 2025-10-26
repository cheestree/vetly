package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.http.api.SupplyApi
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyAssociateInputModel
import com.cheestree.vetly.http.model.input.supply.MedicalSupplyUpdateInputModel
import com.cheestree.vetly.http.model.input.supply.SupplyQueryInputModel
import com.cheestree.vetly.http.model.output.ResponseList
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.service.SupplyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SupplyController(
    private val supplyService: SupplyService,
) : SupplyApi {
    @AuthenticatedRoute
    override fun getAllSupplies(query: SupplyQueryInputModel): ResponseEntity<ResponseList<MedicalSupplyPreview>> =
        ResponseEntity.ok(supplyService.getSupplies(query))

    @ProtectedRoute(VETERINARIAN)
    override fun getClinicSupplies(
        authenticatedUser: AuthenticatedUser,
        clinicId: Long,
        query: SupplyQueryInputModel,
    ): ResponseEntity<ResponseList<MedicalSupplyClinicPreview>> =
        ResponseEntity.ok(supplyService.getClinicSupplies(authenticatedUser, clinicId, query))

    @AuthenticatedRoute
    override fun getSupply(supplyId: Long): ResponseEntity<MedicalSupplyInformation> =
        ResponseEntity.ok(supplyService.getSupply(supplyId))

    @AuthenticatedRoute
    override fun associateSupplyWithClinic(
        clinicId: Long,
        associateSupply: MedicalSupplyAssociateInputModel,
    ): ResponseEntity<Void> {
        supplyService.associateSupplyWithClinic(clinicId, associateSupply)
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun updateSupply(
        clinicId: Long,
        supplyId: Long,
        updatedSupply: MedicalSupplyUpdateInputModel,
    ): ResponseEntity<Void> {
        supplyService.updateSupply(clinicId, supplyId, updatedSupply)
        return ResponseEntity.noContent().build()
    }

    @ProtectedRoute(VETERINARIAN)
    override fun deleteSupply(
        clinicId: Long,
        supplyId: Long,
    ): ResponseEntity<Void> {
        supplyService.deleteSupply(clinicId, supplyId)
        return ResponseEntity.noContent().build()
    }
}
