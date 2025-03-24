package com.cheestree.vetly.http.model.output.animal

import com.cheestree.vetly.http.model.output.user.UserPreview
import java.time.OffsetDateTime

data class AnimalInformation(
    val id: Long,
    val name: String,
    val chip: String?,
    val breed: String,
    val birth: OffsetDateTime?,
    val imageUrl: String?,
    val age: Int?,
    val owner: UserPreview?
)