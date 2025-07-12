package com.cheestree.vetly.http.model.input.guide

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GuideUpdateInputModel(
    @field:NotBlank(message = "Title cannot be blank")
    @field:Size(max = 256, message = "Title must be at most 256 characters long")
    val title: String? = null,
    @field:NotBlank(message = "Description cannot be blank")
    @field:Size(max = 256, message = "Description must be at most 256 characters long")
    val description: String? = null,
    @field:NotBlank
    @field:Size(max = 2048, message = "Description must be at most 2048 characters long")
    val content: String? = null,
)
