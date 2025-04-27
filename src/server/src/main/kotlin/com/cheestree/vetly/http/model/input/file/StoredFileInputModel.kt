package com.cheestree.vetly.http.model.input.file

import jakarta.validation.constraints.NotBlank

data class StoredFileInputModel(
    @field:NotBlank
    val url: String,
    val title: String,
    val description: String?,
)
