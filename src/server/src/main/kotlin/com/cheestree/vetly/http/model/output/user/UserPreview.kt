package com.cheestree.vetly.http.model.output.user

data class UserPreview(
    val id: Long,
    val name: String,
    val imageUrl: String?
)