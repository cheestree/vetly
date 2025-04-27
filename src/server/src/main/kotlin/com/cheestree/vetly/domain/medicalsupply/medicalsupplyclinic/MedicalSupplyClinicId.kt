package com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic

import jakarta.persistence.Embeddable

@Embeddable
data class MedicalSupplyClinicId(
    val medicalSupply: Long,
    val clinic: Long,
)
