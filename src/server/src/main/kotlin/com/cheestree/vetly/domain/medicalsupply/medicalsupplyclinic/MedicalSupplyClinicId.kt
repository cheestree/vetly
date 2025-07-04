package com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class MedicalSupplyClinicId(
    val medicalSupply: Long,
    val clinic: Long,
) : Serializable
