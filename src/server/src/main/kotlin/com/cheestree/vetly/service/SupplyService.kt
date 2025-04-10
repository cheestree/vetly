package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.repository.SupplyRepository
import com.cheestree.vetly.specification.GenericSpecifications.Companion.withFilters
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class SupplyService(
    private val supplyRepository: SupplyRepository,
    private val appConfig: AppConfig
) {
    fun getSupplies(
        clinicId: Long? = null,
        name: String? = null,
        type: String? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<MedicalSupplyInformation> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(appConfig.maxPageSize),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<MedicalSupplyClinic>(
            { root, cb -> clinicId?.let { cb.equal(root.get<Long>("clinicId"), it) } },
            { root, cb -> name?.let { cb.like(cb.lower(root.get("name")), "%$it%") } },
            { root, cb -> type?.let { cb.equal(root.get<String>("type"), it) } }
        )

        return supplyRepository.findAll(specs, pageable).map { it.medicalSupply.asPublic() }
    }

    fun getSupply(supplyId: Long): MedicalSupplyInformation {
        return supplyRepository.findById(supplyId).orElseThrow {
            ResourceNotFoundException("Supply with ID $supplyId not found")
        }.medicalSupply.asPublic()
    }

    fun updateSupply(
        clinicId: Long,
        supplyId: Long,
        quantity: Int? = null,
        price: BigDecimal? = null
    ): MedicalSupplyClinicInformation {
        val supply = supplyRepository.findByMedicalSupplyIdAndClinicId(supplyId, clinicId).orElseThrow {
            ResourceNotFoundException("Supply with ID $supplyId not found")
        }

        val updatedSupply = supply.copy(
            price = price ?: supply.price,
            quantity = quantity ?: supply.quantity
        )

        return supplyRepository.save(updatedSupply).asPublic()
    }

    fun deleteSupply(
        clinicId: Long,
        supplyId: Long
    ): Boolean {
        if(!supplyRepository.existsByClinicIdAndMedicalSupplyId(clinicId, supplyId)) {
            throw ResourceNotFoundException("Supply with ID $supplyId not found")
        }

        return supplyRepository.deleteByClinicIdAndMedicalSupplyId(clinicId, supplyId)
    }
}