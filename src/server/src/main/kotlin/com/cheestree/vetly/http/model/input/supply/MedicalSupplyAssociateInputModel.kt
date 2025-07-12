package com.cheestree.vetly.http.model.input.supply

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Range
import java.math.BigDecimal

data class MedicalSupplyAssociateInputModel(
    @field:Positive(message = "Id must be at least 0")
    val supplyId: Long,
    @field:DecimalMin(value = "0.01", inclusive = true, message = "Price must be positive")
    @field:Digits(integer = 5, fraction = 2, message = "Price must be of format #####.##")
    val price: BigDecimal,
    @field:Range(min = 0, message = "Quantity must be positive")
    val quantity: Int,
)
