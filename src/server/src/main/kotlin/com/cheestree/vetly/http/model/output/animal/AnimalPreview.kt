package com.cheestree.vetly.http.model.output.animal

import com.cheestree.vetly.http.model.output.user.UserPreview
import java.time.OffsetDateTime

data class AnimalPreview(
    val id: Long,
    val name: String,
    val species: String?,
    val birthDate: OffsetDateTime?,
    val imageUrl: String?,
    val age: Int?,
    val owner: UserPreview?,
)
