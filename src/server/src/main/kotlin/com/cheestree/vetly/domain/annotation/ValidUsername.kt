package com.cheestree.vetly.domain.annotation

import com.cheestree.vetly.domain.validator.UsernameValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [UsernameValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidUsername(
    val message: String = "Invalid username. Length must be between 1 and 16 characters.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
