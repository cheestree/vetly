package com.cheestree.vetly.http.model.input.guide

data class GuideUpdateInputModel(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val text: String
)