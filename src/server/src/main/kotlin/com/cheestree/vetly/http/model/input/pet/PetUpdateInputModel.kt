package com.cheestree.vetly.http.model.input.pet

import java.time.OffsetDateTime

data class PetUpdateInputModel(
    val petId: Long?,
    val name: String?,
    val birth: OffsetDateTime?,
    val breed: String?,
    val imageUrl: String?,
    val ownerId: Long?
)
