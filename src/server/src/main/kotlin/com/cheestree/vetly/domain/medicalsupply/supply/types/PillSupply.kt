package com.cheestree.vetly.domain.medicalsupply.supply.types

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "pill_supply", schema = "vetly")
class PillSupply(
    name: String,
    description: String?,
    imageUrl: String?,
    clinics: Set<MedicalSupplyClinic> = emptySet(),

    @Column(nullable = false)
    val pillsPerBox: Int,

    @Column(nullable = false)
    val mgPerPill: Double
) : MedicalSupply(name = name, description = description, imageUrl = imageUrl) {
    override fun copy(
        id: Long,
        name: String,
        description: String?,
        imageUrl: String?
    ): PillSupply {
        return PillSupply(
            name = name,
            description = description,
            imageUrl = imageUrl,
            pillsPerBox = pillsPerBox,
            mgPerPill = mgPerPill
        )
    }
}
