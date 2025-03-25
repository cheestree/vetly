package com.cheestree.vetly.http.model.input.guide

data class GuideCreateInputModel(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val text: String
)