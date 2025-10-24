package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.annotation.Microchip
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import org.openapitools.jackson.nullable.JsonNullable
import java.time.OffsetDateTime

data class AnimalUpdateInputModel(
    @field:Pattern(regexp = "[a-zA-Z\\s-]*", message = "Name can only contain letters, spaces, and hyphens")
    val name: JsonNullable<String> = JsonNullable.undefined(),
    @field:Microchip
    val microchip: JsonNullable<String?> = JsonNullable.undefined(),
    val sex: JsonNullable<Sex> = JsonNullable.undefined(),
    val sterilized: JsonNullable<Boolean> = JsonNullable.undefined(),
    @field:Pattern(regexp = "[a-zA-Z\\s-]*", message = "Species can only contain letters, spaces, and hyphens")
    val species: JsonNullable<String> = JsonNullable.undefined(),
    @field:PastOrPresent(message = "Birth date must not be in the future")
    val birthDate: JsonNullable<OffsetDateTime> = JsonNullable.undefined(),
    @field:Email(message = "Must be a valid email address")
    val ownerEmail: JsonNullable<String> = JsonNullable.undefined(),
)
