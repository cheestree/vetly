package com.cheestree.vetly.http.model.output.guide

data class GuideInformation(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val description: String,
    val content: String?,
    val createdAt: String,
    val updatedAt: String?
)