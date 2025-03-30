package com.cheestree.vetly.domain.user.roles

enum class Status {
    PENDING,
    APPROVED,
    REJECTED;

    companion object {
        fun fromString(value: String): Status {
            return when (value.lowercase().trim()) {
                "pending" -> PENDING
                "approved" -> APPROVED
                "rejected" -> REJECTED
                else -> throw IllegalArgumentException("Unknown status: $value")
            }
        }
    }
}