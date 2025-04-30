package com.cheestree.vetly.http.model.input.user

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.Date

data class UserUpdateInputModel(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val username: String,
    @field:Pattern(
        regexp = "^(https?://.*)?$",
        message = "Must be a valid URL or null",
    )
    val imageUrl: String? = null,
    @field:Min(100000000)
    @field:Max(999999999)
    val phone: Int? = null,
    @field:Past
    val birthDate: Date? = null,
)
