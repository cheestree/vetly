package com.cheestree.vetly.converter

import com.cheestree.vetly.domain.user.roles.Role
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class RoleListConverter : AttributeConverter<List<Role>, String> {
    override fun convertToDatabaseColumn(attribute: List<Role>?): String? {
        return attribute?.joinToString(",") { it.name }
    }
    override fun convertToEntityAttribute(dbData: String?): List<Role> {
        return dbData?.trim('{', '}')?.split(",")?.map { Role.valueOf(it) } ?: emptyList()
    }
}