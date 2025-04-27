package com.cheestree.vetly.domain

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@MappedSuperclass
abstract class BaseEntity {
    @Column(name = "created_at", updatable = false)
    lateinit var createdAt: OffsetDateTime

    @Column(name = "updated_at")
    lateinit var updatedAt: OffsetDateTime

    @PrePersist
    fun onCreate() {
        createdAt = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        updatedAt = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)
    }
}
