package com.cheestree.vetly.domain.medicalsupply.supply

import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
import com.cheestree.vetly.http.model.output.supply.LiquidSupplyInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyInformation
import com.cheestree.vetly.http.model.output.supply.PillSupplyInformation
import com.cheestree.vetly.http.model.output.supply.ShotSupplyInformation
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = PillSupply::class, name = "pill"),
    JsonSubTypes.Type(value = LiquidSupply::class, name = "liquid"),
    JsonSubTypes.Type(value = ShotSupply::class, name = "shot")
)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "medical_supply", schema = "vetly")
abstract class MedicalSupply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    var description: String? = null,
    var imageUrl: String? = null
){
    fun updateWith(
        name: String?,
        description: String?,
        imageUrl: String?
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        imageUrl?.let { this.imageUrl = it }
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