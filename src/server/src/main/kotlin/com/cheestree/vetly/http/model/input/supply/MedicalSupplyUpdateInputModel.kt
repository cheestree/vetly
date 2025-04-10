package com.cheestree.vetly.http.model.input.supply

import java.math.BigDecimal

data class MedicalSupplyUpdateInputModel(
    val price: BigDecimal?,
    val quantity: Int?
)