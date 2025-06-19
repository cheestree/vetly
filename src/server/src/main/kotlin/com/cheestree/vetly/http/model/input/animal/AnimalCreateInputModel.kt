package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.annotation.ValidMicrochip
import com.cheestree.vetly.domain.annotation.ValidUsername
import java.time.OffsetDateTime
import java.util.UUID

data class AnimalCreateInputModel(
    @field:ValidUsername
    val name: String,
    @field:ValidMicrochip
    val microchip: String?,
    val sex: Sex,
    val sterilized: Boolean,
    val species: String?,
    val birthDate: OffsetDateTime?,
    val ownerId: UUID? = null,
)
