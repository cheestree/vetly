package com.cheestree.vetly.domain.medicalsupply.supply.types

import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "shot_supplies", schema = "vetly")
class ShotSupply(
    id: Long = 0,
    name: String,
    description: String?,
    imageUrl: String?,
    @Column(nullable = false)
    val vialsPerBox: Int,
    @Column(nullable = false)
    val mlPerVial: Double,
) : MedicalSupply(id = id, name = name, description = description, imageUrl = imageUrl, type = SupplyType.SHOT)
