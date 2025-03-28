package com.cheestree.vetly.domain.medicalsupply.supply.types

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "liquid_supply", schema = "vetly")
class LiquidSupply(
    name: String,
    description: String?,
    imageUrl: String?,
    clinics: Set<MedicalSupplyClinic> = emptySet(),

    @Column(nullable = false)
    val mlPerBottle: Double,

    @Column(nullable = false)
    val mlDosePerUse: Double
) : MedicalSupply(name = name, description = description, imageUrl = imageUrl)
