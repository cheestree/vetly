package com.cheestree.vetly.domain.medicalsupply.supply

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.http.model.output.supply.*
import jakarta.persistence.*

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
    val type: SupplyType
) : BaseEntity() {
    fun updateWith(
        name: String?,
        description: String?,
        imageUrl: String?
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        imageUrl?.let { this.imageUrl = it }
    }

    fun asPreview(): MedicalSupplyPreview {
        return MedicalSupplyPreview(
            id = this.id,
            name = this.name,
            type = this.type
        )
    }

    fun asPublic(): MedicalSupplyInformation = when (this) {
        is ShotSupply -> ShotSupplyInformation(id, name, description, imageUrl, vialsPerBox, mlPerVial)
        is PillSupply -> PillSupplyInformation(id, name, description, imageUrl, pillsPerBox, mgPerPill)
        is LiquidSupply -> LiquidSupplyInformation(id, name, description, imageUrl, mlPerBottle, mlDosePerUse)
        else -> {
            throw IllegalArgumentException("Unknown MedicalSupply type: ${this::class.simpleName}")
        }
    }
}