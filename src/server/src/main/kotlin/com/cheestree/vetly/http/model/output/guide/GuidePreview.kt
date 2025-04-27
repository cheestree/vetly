package com.cheestree.vetly.http.model.output.guide

import java.time.OffsetDateTime

data class GuidePreview(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val description: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
)
