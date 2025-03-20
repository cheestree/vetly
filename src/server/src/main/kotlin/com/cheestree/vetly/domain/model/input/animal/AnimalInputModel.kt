package com.cheestree.vetly.domain.model.input.animal

import java.time.OffsetDateTime

data class AnimalInputModel(
    val name: String,
    val microchip: String?,
    val birth: OffsetDateTime?,
    val breed: String,
    val imageUrl: String
)