package com.cheestree.vetly.http

import com.cheestree.vetly.http.model.input.request.RequestExtraData
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.springframework.stereotype.Component

@Component
class RequestMapper(
    val objectMapper: ObjectMapper,
    private val validator: Validator
) {
    fun <T : Any> validateData(data: T): T {
        val violations = validator.validate(data)
        if (violations.isNotEmpty())
            throw ConstraintViolationException(violations)
        return data
    }

    fun requestExtraDataToMap(extraData: RequestExtraData?): Map<String, Any>? {
        return extraData?.let {
            objectMapper.convertValue(it, object : TypeReference<Map<String, Any>>() {})
        }
    }
}