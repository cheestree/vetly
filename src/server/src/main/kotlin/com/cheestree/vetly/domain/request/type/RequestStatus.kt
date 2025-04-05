package com.cheestree.vetly.domain.request.type

enum class RequestStatus {
    PENDING,
    APPROVED,
    REJECTED;

    companion object {
        fun fromString(value: String): RequestStatus {
            return when (value.lowercase().trim()) {
                "pending" -> PENDING
                "approved" -> APPROVED
                "rejected" -> REJECTED
                else -> throw IllegalArgumentException("Unknown requestStatus: $value")
            }
        }
    }
}