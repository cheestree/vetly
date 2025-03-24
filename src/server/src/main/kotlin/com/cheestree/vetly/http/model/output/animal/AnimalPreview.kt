package com.cheestree.vetly.http.model.output.animal

data class AnimalPreview(
    val id: Long,
    val name: String,
    val imageUrl: String?
)