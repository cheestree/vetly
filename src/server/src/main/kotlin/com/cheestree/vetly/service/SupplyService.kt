package com.cheestree.vetly.service

import com.cheestree.vetly.AppConfig
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.exception.VetException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.repository.ClinicRepository
import com.cheestree.vetly.repository.MedicalSupplyRepository
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
    private val medicalSupplyRepository: MedicalSupplyRepository,
    private val clinicRepository: ClinicRepository,
    private val appConfig: AppConfig
) {
    fun getClinicSupplies(
        user: AuthenticatedUser,
        clinicId: Long,
        name: String? = null,
        type: SupplyType? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "medicalSupply.name",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<MedicalSupplyClinicPreview> {
        val clinic = clinicRepository.findById(clinicId).orElseThrow {
            ResourceNotFoundException("Clinic with ID $clinicId not found")
        }

        if (!clinic.clinicMemberships.any { it.veterinarian.id == user.id && it.leftIn == null }) {
            throw VetException.UnauthorizedAccessException("User ${user.id} is not a member of clinic $clinicId")
        }

        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(appConfig.maxPageSize),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<MedicalSupplyClinic>(
            { root, cb -> clinicId.let {
                cb.equal(root.get<Clinic>("clinic").get<String>("id"), it)
            } },
            { root, cb -> name?.let {
                val medicalSupply = root.get<MedicalSupply>("medicalSupply")
                cb.like(cb.lower(medicalSupply.get("name")), "%${it.lowercase()}%")
            } },
            { root, cb -> type?.let {
                val medicalSupply = root.get<MedicalSupply>("medicalSupply")
                cb.equal(medicalSupply.get<SupplyType>("type"), it)
            } }
        )

        return supplyRepository.findAll(specs, pageable).map { it.asPreview() }
    }

    fun getSupplies(
        name: String? = null,
        type: SupplyType? = null,
        page: Int = 0,
        size: Int = appConfig.defaultPageSize,
        sortBy: String = "name",
        sortDirection: Sort.Direction = Sort.Direction.DESC
    ): Page<MedicalSupplyPreview> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceAtMost(appConfig.maxPageSize),
            Sort.by(sortDirection, sortBy)
        )

        val specs = withFilters<MedicalSupply>(
            { root, cb -> name?.let {
                cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%")
            } },
            { root, cb -> type?.let {
                cb.equal(root.get<SupplyType>("type"), it)
            } }
        )

        return medicalSupplyRepository.findAll(specs, pageable).map { it.asPreview() }
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
        val supply = supplyRepository.findByClinicIdAndMedicalSupplyId(clinicId, supplyId).orElseThrow {
            ResourceNotFoundException("Supply with ID $supplyId not found")
        }

        supply.updateWith(
            quantity = quantity,
            price = price
        )

        return supplyRepository.save(supply).asPublic()
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