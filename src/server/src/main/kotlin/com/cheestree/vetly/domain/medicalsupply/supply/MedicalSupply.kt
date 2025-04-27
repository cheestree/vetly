package com.cheestree.vetly.domain.medicalsupply.supply

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.http.model.output.supply.LiquidSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.http.model.output.supply.PillSupplyInformation
import com.cheestree.vetly.http.model.output.supply.ShotSupplyInformation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "medical_supplies", schema = "vetly")
abstract class MedicalSupply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    var name: String,
    var description: String? = null,
    var imageUrl: String? = null,
    @Enumerated(EnumType.STRING)
    val type: SupplyType,
) : BaseEntity() {
    fun updateWith(
        name: String?,
        description: String?,
        imageUrl: String?,
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        imageUrl?.let { this.imageUrl = it }
    }

    fun asPreview(): MedicalSupplyPreview {
        return MedicalSupplyPreview(
            id = this.id,
            name = this.name,
            type = this.type,
        )
    }

    fun asPublic(): MedicalSupplyInformation =
        when (this) {
            is ShotSupply -> ShotSupplyInformation(id, name, description, imageUrl, vialsPerBox, mlPerVial)
            is PillSupply -> PillSupplyInformation(id, name, description, imageUrl, pillsPerBox, mgPerPill)
            is LiquidSupply -> LiquidSupplyInformation(id, name, description, imageUrl, mlPerBottle, mlDosePerUse)
            else -> {
                throw IllegalArgumentException("Unknown MedicalSupply type: ${this::class.simpleName}")
            }
        }
}
