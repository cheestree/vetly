package com.cheestree.vetly.http.model.input.supply

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class MedicalSupplyUpdateInputModel(
    @field:DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
    val price: BigDecimal?,
    @field:Min(value = 0, message = "Quantity must be a positive number")
    val quantity: Int?,
)
