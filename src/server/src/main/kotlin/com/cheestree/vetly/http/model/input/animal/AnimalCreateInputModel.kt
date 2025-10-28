package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.annotation.Microchip
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import java.time.OffsetDateTime

data class AnimalCreateInputModel(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Pattern(regexp = "[a-zA-Z\\s-]*", message = "Name can only contain letters, spaces, and hyphens")
    val name: String,
    @field:Microchip
    val microchip: String? = null,
    @field:NotNull(message = "Sex must not be null")
    var sex: Sex = Sex.UNKNOWN,
    @field:NotNull(message = "Sterilized must be true or false")
    var sterilized: Boolean = true,
    @field:Pattern(regexp = "[a-zA-Z]*", message = "Species can only have letters")
    val species: String? = null,
    @field:PastOrPresent(message = "Birth date must not be in the future.")
    val birthDate: OffsetDateTime? = null,
    @field:Email(message = "Must be a valid email address")
    val ownerEmail: String? = null,
)
