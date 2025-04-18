package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.annotation.ValidMicrochip
import com.cheestree.vetly.domain.annotation.ValidUsername
import jakarta.annotation.Nullable
import java.time.OffsetDateTime

data class AnimalCreateInputModel(
    @field:ValidUsername
    val name: String,

    @field:ValidMicrochip
    val microchip: String?,

    val birthDate: OffsetDateTime?,

    val species: String?,

    val imageUrl: String?
)