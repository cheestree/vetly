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
    open var createdAt: OffsetDateTime = generateTimestamp()

    @Column(name = "updated_at")
    open var updatedAt: OffsetDateTime = generateTimestamp()

    private fun generateTimestamp(): OffsetDateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)

    @PrePersist
    fun onCreate() {
        val now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)
    }
}
