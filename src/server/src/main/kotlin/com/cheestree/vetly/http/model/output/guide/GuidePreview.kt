package com.cheestree.vetly.http.model.output.guide

import com.cheestree.vetly.http.model.output.user.UserPreview
import java.time.OffsetDateTime

data class GuidePreview(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val description: String,
    val author: UserPreview,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
)
