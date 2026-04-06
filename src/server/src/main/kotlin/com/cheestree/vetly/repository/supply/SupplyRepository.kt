package com.cheestree.vetly.repository.supply

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinicId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SupplyRepository :
    JpaRepository<MedicalSupplyClinic, MedicalSupplyClinicId>,
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
    ): Long
}
