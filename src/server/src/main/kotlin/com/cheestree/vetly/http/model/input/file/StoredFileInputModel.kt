package com.cheestree.vetly.http.model.input.file

import jakarta.validation.constraints.NotBlank
import org.springframework.web.multipart.MultipartFile

data class StoredFileInputModel(
    @field:NotBlank
    val file: MultipartFile,
    val title: String,
    val description: String?,
)
