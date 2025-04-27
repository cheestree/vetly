package com.cheestree.vetly.http.model.output.supply

import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType

data class MedicalSupplyPreview(
    val id: Long,
    val name: String,
    val type: SupplyType,
)
