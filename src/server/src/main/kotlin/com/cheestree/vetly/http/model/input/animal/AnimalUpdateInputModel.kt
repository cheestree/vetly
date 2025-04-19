package com.cheestree.vetly.http.model.input.animal

import java.time.OffsetDateTime

data class AnimalUpdateInputModel(
    val name: String?,

    val microchip: String?,

    val birthDate: OffsetDateTime?,

    val species: String?,

    val imageUrl: String?,

    val ownerId: Long?
)