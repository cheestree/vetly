package com.cheestree.vetly.domain.medicalsupply.supply.types

import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table

@Entity
@Table(name = "pill_supplies", schema = "vetly")
@PrimaryKeyJoinColumn(name = "id")
class PillSupply(
    id: Long = 0,
    name: String,
    description: String?,
    imageUrl: String?,
    @Column(nullable = false)
    val pillsPerBox: Int,
    @Column(nullable = false)
    val mgPerPill: Double,
) : MedicalSupply(id = id, name = name, description = description, imageUrl = imageUrl, type = SupplyType.PILL)
