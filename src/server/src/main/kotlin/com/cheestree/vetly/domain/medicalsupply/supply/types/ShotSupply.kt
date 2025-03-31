package com.cheestree.vetly.domain.medicalsupply.supply.types

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "shot_supply", schema = "vetly")
class ShotSupply(
    name: String,
    description: String?,
    imageUrl: String?,
    clinics: Set<MedicalSupplyClinic> = emptySet(),

    @Column(nullable = false)
    val vialsPerBox: Int,

    @Column(nullable = false)
    val mlPerVial: Double
) : MedicalSupply(name = name, description = description, imageUrl = imageUrl){
    override fun copy(
        id: Long,
        name: String,
        description: String?,
        imageUrl: String?
    ): ShotSupply {
        return ShotSupply(
            name = name,
            description = description,
            imageUrl = imageUrl,
            vialsPerBox = vialsPerBox,
            mlPerVial = mlPerVial
        )
    }
}