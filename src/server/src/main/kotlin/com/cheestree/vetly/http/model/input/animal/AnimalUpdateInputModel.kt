package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.animal.sex.Sex
import jakarta.annotation.Nullable
import jakarta.validation.constraints.Email
import java.time.OffsetDateTime

data class AnimalUpdateInputModel(
    val name: String?,
    val microchip: String?,
    val sex: Sex?,
    val sterilized: Boolean?,
    val species: String?,
    val birthDate: OffsetDateTime?,
    @field:Nullable
    @field:Email(message = "Correct email format is required")
    val ownerEmail: String?,
)
