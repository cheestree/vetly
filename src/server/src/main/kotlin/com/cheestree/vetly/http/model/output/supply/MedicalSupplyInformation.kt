package com.cheestree.vetly.http.model.output.supply

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ShotSupplyInformation::class, name = "shot"),
    JsonSubTypes.Type(value = LiquidSupplyInformation::class, name = "liquid"),
    JsonSubTypes.Type(value = PillSupplyInformation::class, name = "pill"),
)
sealed class MedicalSupplyInformation(
    open val id: Long,
    open val name: String,
    open val description: String?,
    open val imageUrl: String?,
)
