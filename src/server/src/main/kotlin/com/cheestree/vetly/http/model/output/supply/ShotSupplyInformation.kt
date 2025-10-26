package com.cheestree.vetly.http.model.output.supply

data class ShotSupplyInformation(
    override val id: Long,
    override val name: String,
    override val description: String?,
    override val imageUrl: String?,
    val vialsPerBox: Int,
    val mlPerVial: Double,
) : MedicalSupplyInformation(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
)
