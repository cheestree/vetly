package com.cheestree.vetly.domain.medicalsupply.supply

import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyPreview
import com.cheestree.vetly.http.model.output.supply.LiquidSupplyInformation
import com.cheestree.vetly.http.model.output.supply.PillSupplyInformation
import com.cheestree.vetly.http.model.output.supply.ShotSupplyInformation
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.OneToOne
import jakarta.persistence.CascadeType
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "medical_supplies", schema = "vetly")
abstract class MedicalSupply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0,
    @Column(nullable = false)
    open var name: String,
    open var description: String? = null,
    open var imageUrl: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var type: SupplyType,
) {
    fun updateWith(
        name: String?,
        description: String?,
        imageUrl: String?,
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        imageUrl?.let { this.imageUrl = it }
    }

    fun asPreview(): MedicalSupplyPreview =
        MedicalSupplyPreview(
            id = this.id,
            name = this.name,
            type = this.type,
        )

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
