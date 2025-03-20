package com.cheestree.vetly.domain.model.input.pet

import com.cheestree.vetly.domain.annotation.ValidMicrochip
import com.cheestree.vetly.domain.annotation.ValidUsername
import java.time.OffsetDateTime

data class PetInputModel(
    @field:ValidUsername
    val name: String,

    @field:ValidMicrochip
    val microchip: String,

    val birth: OffsetDateTime? = null,

    val breed: String? = null,

    val imageUrl: String? = null,

    val ownerId: Long
)