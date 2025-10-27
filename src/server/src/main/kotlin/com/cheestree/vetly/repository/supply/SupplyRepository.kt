package com.cheestree.vetly.repository.supply

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SupplyRepository :
    JpaRepository<MedicalSupplyClinic, Long>,
    JpaSpecificationExecutor<MedicalSupplyClinic> {
    fun findByClinicIdAndMedicalSupplyId(
        clinicId: Long,
        supplyId: Long,
    ): Optional<MedicalSupplyClinic>

    fun existsByClinicIdAndMedicalSupplyId(
        clinicId: Long,
        medicalSupplyId: Long,
    ): Boolean

    fun deleteByClinicIdAndMedicalSupplyId(
        clinicId: Long,
        supplyId: Long,
    ): Boolean
}