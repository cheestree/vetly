package com.cheestree.vetly.http.model.output.supply

import java.math.BigDecimal

data class MedicalSupplyClinicInformation(
    val id: Long,
    val name: String,
    val description: MedicalSupplyInformation,
    val quantity: Int,
    val price: BigDecimal,
    val type: String?,
)
