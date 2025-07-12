package com.cheestree.vetly.domain.annotation

import com.cheestree.vetly.domain.validator.MicrochipValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [MicrochipValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Microchip(
    val message: String = "Invalid microchip ID. Length must be either 9, 10 or 15-digit number.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
