package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.annotation.Microchip
import jakarta.validation.constraints.*
import java.time.OffsetDateTime

data class AnimalUpdateInputModel(
    @field:Pattern(regexp = "[a-zA-Z\\s-]*", message = "Name can only contain letters, spaces, and hyphens")
    val name: String? = null,
    @field:Microchip
    val microchip: String? = null,
    val sex: Sex? = null,
    val sterilized: Boolean? = null,
    @field:Pattern(regexp = "[a-zA-Z\\s-]*", message = "Species can only contain letters, spaces, and hyphens")
    val species: String? = null,
    @field:PastOrPresent(message = "Birth date must not be in the future")
    val birthDate: OffsetDateTime? = null,
    @field:Email(message = "Must be a valid email address")
    val ownerEmail: String? = null,
)
