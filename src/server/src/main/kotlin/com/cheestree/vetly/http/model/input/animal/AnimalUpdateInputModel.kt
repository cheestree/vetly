package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.animal.sex.Sex
import java.time.OffsetDateTime
import java.util.UUID

data class AnimalUpdateInputModel(
    val name: String?,
    val microchip: String?,
    val sex: Sex?,
    val sterilized: Boolean?,
    val species: String?,
    val birthDate: OffsetDateTime?,
    val ownerId: UUID?,
)
