package com.cheestree.vetly.http.model.output.supply

data class LiquidSupplyInformation(
    override val id: Long,
    override val name: String,
    override val description: String?,
    override val imageUrl: String?,
    val mlPerBottle: Double,
    val mlDosePerUse: Double
) : MedicalSupplyInformation(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl
)