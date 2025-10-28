package com.cheestree.vetly.http.model.output.supply

data class PillSupplyInformation(
    override val id: Long,
    override val name: String,
    override val description: String?,
    override val imageUrl: String?,
    val pillsPerBox: Int,
    val mgPerPill: Double,
) : MedicalSupplyInformation(
        id = id,
        name = name,
        description = description,
        imageUrl = imageUrl,
    )
