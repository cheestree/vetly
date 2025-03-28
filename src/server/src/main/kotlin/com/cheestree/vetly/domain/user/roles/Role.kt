package com.cheestree.vetly.domain.user.roles

enum class Role(val dbName: String) {
    VETERINARIAN("VETERINARIAN"),
    ADMIN("ADMIN");

    companion object {
        fun fromDbValue(value: String): Role? = entries.find { it.dbName == value }
    }
}