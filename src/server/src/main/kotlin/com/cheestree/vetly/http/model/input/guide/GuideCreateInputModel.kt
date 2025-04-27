package com.cheestree.vetly.http.model.input.guide

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GuideCreateInputModel(
    @field:Size(max = 256, message = "Title must be at most 256 characters long")
    val title: String,
    @field:Size(max = 256, message = "Description must be at most 256 characters long")
    val description: String,
    val imageUrl: String?,
    @field:NotBlank
    val content: String,
)
