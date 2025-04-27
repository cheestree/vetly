package com.cheestree.vetly.domain.validator

import com.cheestree.vetly.domain.annotation.ValidUsername
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UsernameValidator : ConstraintValidator<ValidUsername, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return false
        val sanitizedValue = value.trim().replace(" ", "")
        if (sanitizedValue.isEmpty()) return false
        return sanitizedValue.length in 1..16
    }
}
