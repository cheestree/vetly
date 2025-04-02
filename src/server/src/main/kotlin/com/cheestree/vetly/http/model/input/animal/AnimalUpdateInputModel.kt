package com.cheestree.vetly.http.model.input.animal

import jakarta.annotation.Nullable
import java.time.OffsetDateTime

data class AnimalUpdateInputModel(
    @field:Nullable
    val name: String?,

    @field:Nullable
    val microchip: String?,

    @field:Nullable
    val birthDate: OffsetDateTime?,

    @field:Nullable
    val breed: String?,

    @field:Nullable
    val imageUrl: String?
)