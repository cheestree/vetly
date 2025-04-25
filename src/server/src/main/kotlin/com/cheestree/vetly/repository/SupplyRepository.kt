package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SupplyRepository: JpaRepository<MedicalSupplyClinic, Long>, JpaSpecificationExecutor<MedicalSupplyClinic> {
    fun findByMedicalSupplyIdAndClinicId(clinicId: Long, supplyId: Long): Optional<MedicalSupplyClinic>
    fun existsByClinicIdAndMedicalSupplyId(medicalSupplyId: Long, clinicId: Long): Boolean
    fun deleteByClinicIdAndMedicalSupplyId(clinicId: Long, supplyId: Long): Boolean
}