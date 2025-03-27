package com.cheestree.vetly.http.model.output.file

import java.time.OffsetDateTime
import java.util.UUID

data class StoredFileInformation(
    val uuid: UUID,
    val url: String,
    val description: String?,
    val createdAt: OffsetDateTime
)