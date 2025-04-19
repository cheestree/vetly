package com.cheestree.vetly.domain.medicalsupply.supply.types

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "shot_supply", schema = "vetly")
class ShotSupply(
    id: Long = 0,
    name: String,
    description: String?,
    imageUrl: String?,
    clinics: Set<MedicalSupplyClinic> = emptySet(),

    @Column(nullable = false)
    val vialsPerBox: Int,

    @Column(nullable = false)
    val mlPerVial: Double
) : MedicalSupply(id = id, name = name, description = description, imageUrl = imageUrl)