package com.cheestree.vetly.http.model.input.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

data class UserUpdateInputModel(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val username: String,
    @field:Pattern(
        regexp = "^(https?://.*)?$",
        message = "Must be a valid URL or null",
    )
    val imageUrl: String? = null,
    @field:Pattern(regexp = "^\\d{9}$")
    val phone: String? = null,
    @field:Past
    val birthDate: Date? = null,
)
