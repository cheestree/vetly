package com.cheestree.vetly.domain.validator

import com.cheestree.vetly.domain.annotation.ValidMicrochip
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MicrochipValidator : ConstraintValidator<ValidMicrochip, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return false
        if (value.any { it !in '0'..'9' }) return false
        val length = value.length
        return length == 9 || length == 10 || length == 15
    }
}