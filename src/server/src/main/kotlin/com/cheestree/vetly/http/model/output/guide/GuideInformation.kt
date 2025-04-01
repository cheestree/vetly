package com.cheestree.vetly.http.model.output.guide

import java.time.OffsetDateTime

data class GuideInformation(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val description: String,
    val content: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?
)