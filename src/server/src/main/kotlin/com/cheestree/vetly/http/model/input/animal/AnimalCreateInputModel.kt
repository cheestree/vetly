package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.annotation.ValidMicrochip
import com.cheestree.vetly.domain.annotation.ValidUsername
import jakarta.annotation.Nullable
import java.time.OffsetDateTime

data class AnimalCreateInputModel(
    @field:ValidUsername
    val name: String,

    @field:Nullable
    @field:ValidMicrochip
    val microchip: String?,

    @field:Nullable
    val birth: OffsetDateTime?,

    @field:Nullable
    val breed: String?,

    @field:Nullable
    val imageUrl: String?
)