package com.cheestree.vetly.http.model.output.clinic

import com.cheestree.vetly.http.model.output.file.FilePreview

data class ClinicLink(
    val id: Long,
    val name: String,
    val image: FilePreview?,
)
