package com.cheestree.vetly.http.model.input.supply

import java.math.BigDecimal

data class MedicalSupplyAssociateInputModel (
    val supplyId: Long,
    val price: BigDecimal,
    val quantity: Int
)