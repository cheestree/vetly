package com.cheestree.vetly.domain.validator

import com.cheestree.vetly.domain.annotation.Microchip
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MicrochipValidator : ConstraintValidator<Microchip, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return true
        if (value.any { it !in '0'..'9' }) return false
        val length = value.length
        return length == 9 || length == 10 || length == 15
    }
}
