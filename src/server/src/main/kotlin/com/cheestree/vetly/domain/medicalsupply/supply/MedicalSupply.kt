package com.cheestree.vetly.domain.medicalsupply.supply

import com.cheestree.vetly.domain.medicalsupply.supply.types.LiquidSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.PillSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.ShotSupply
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
    val name: String,

    val description: String? = null,
    val imageUrl: String? = null
)