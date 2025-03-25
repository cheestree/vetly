package com.cheestree.vetly.http.model.input.animal

import java.time.OffsetDateTime

data class AnimalUpdateInputModel(
    val name: String?,
    val microchip: String?,
    val birth: OffsetDateTime?,
    val breed: String?,
    val imageUrl: String?
)