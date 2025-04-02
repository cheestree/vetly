package com.cheestree.vetly.converter

import com.cheestree.vetly.domain.user.roles.Role
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class RoleListConverter : AttributeConverter<Set<Role>, String> {
    override fun convertToDatabaseColumn(attribute: Set<Role>?): String? {
        return attribute?.joinToString(",") { it.name }
    }
    override fun convertToEntityAttribute(dbData: String?): Set<Role> {
        return dbData?.trim('{', '}')?.split(",")?.mapTo(mutableSetOf()) { Role.valueOf(it) } ?: emptySet()
    }
}