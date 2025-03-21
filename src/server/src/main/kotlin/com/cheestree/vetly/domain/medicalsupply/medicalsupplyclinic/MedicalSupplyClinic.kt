package com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.medicalsupply.MedicalSupply
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "medical_supply_clinic", schema = "vetly")
class MedicalSupplyClinic (
    @EmbeddedId
    val id: MedicalSupplyClinicId,

    @ManyToOne
    @MapsId("medicalSupply")
    val medicalSupply: MedicalSupply,

    @ManyToOne
    @MapsId("clinic")
    val clinic: Clinic,

    @Column(nullable = false)
    val price: BigDecimal,

    @Column(nullable = false)
    val count: Int
)