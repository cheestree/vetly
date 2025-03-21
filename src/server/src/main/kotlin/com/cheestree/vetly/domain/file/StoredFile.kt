package com.cheestree.vetly.domain.file

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "files", schema = "vetly")
class StoredFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, updatable = false)
    val uuid: UUID = UUID.randomUUID(),

    val url: String,
    val description: String? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)