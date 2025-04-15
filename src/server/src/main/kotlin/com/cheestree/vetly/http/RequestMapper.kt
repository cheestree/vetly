package com.cheestree.vetly.http

import com.cheestree.vetly.domain.exception.VetException.BadRequestException
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestAction.CREATE
import com.cheestree.vetly.domain.request.type.RequestAction.UPDATE
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.request.type.RequestTarget.CLINIC
import com.cheestree.vetly.domain.request.type.RequestTarget.ROLE
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DatabindException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.springframework.stereotype.Component

@Component
class RequestMapper(
    val objectMapper: ObjectMapper,
    private val validator: Validator
) {
    /**
     * Validates if the extraData can be parsed based on target and action,
     * without returning the parsed object
     */
    fun validateParseable(extraData: String?, target: RequestTarget, action: RequestAction): Boolean {
        if (extraData == null) return true

        return try {
            when (target to action) {
                CLINIC to CREATE -> objectMapper.readValue(extraData, ClinicCreateInputModel::class.java)
                ROLE to UPDATE -> objectMapper.readValue(extraData, Role::class.java)
                // other combinations
                else -> throw BadRequestException("Unsupported request target/action combination: ${target.name} to ${action.name}")
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Alternative that throws exceptions with useful messages instead of returning boolean
     */
    fun validateOrThrowAndReturn(extraData: String?, target: RequestTarget, action: RequestAction): Any? {
        if (extraData == null) return null

        return try {
            when (target to action) {
                CLINIC to CREATE -> validateJson(extraData, objectMapper, ClinicCreateInputModel::class.java)
                ROLE to UPDATE -> validateJson(extraData, objectMapper, Role::class.java)
                else -> throw BadRequestException("Unsupported request target/action combination: ${target.name} to ${action.name}")
            }
        } catch (e: DatabindException) {
            throw BadRequestException("Invalid format for ${target.name} ${action.name}: ${e.message}")
        }
    }

    fun <T : Any> validateJson(json: String, mapper: ObjectMapper, type: Class<T>): T {
        val obj = mapper.readValue(json, type)
        val violations = validator.validate(obj)
        if (violations.isNotEmpty())
            throw ConstraintViolationException(violations)
        return obj
    }

    fun returnAsMap(data: Any?): Map<String, Any> {
        return objectMapper.convertValue(data, object : TypeReference<Map<String, Any>>() {})
    }
}