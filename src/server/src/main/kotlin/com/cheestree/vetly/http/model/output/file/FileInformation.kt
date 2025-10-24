package com.cheestree.vetly.http.model.output.file

import java.time.OffsetDateTime
import java.util.*

data class FileInformation(
    val id: UUID?,
    val url: String,
    val name: String?,
    val description: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
)
