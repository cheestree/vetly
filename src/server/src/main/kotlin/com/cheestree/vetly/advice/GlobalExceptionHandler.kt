package com.cheestree.vetly.advice

import com.cheestree.vetly.domain.error.ApiError
import com.cheestree.vetly.domain.error.ErrorDetail
import com.cheestree.vetly.domain.exception.VetException.*
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [
        UnauthorizedAccessException::class,
        InsufficientPermissionException::class,
        ResourceAlreadyExistsException::class,
        InvalidInputException::class,
        NumberFormatException::class,
        ResourceNotFoundException::class
    ])
    fun handleSimpleExceptions(ex: Exception): ResponseEntity<ApiError> {
        val httpStatus = when(ex) {
            is UnauthorizedAccessException, is InsufficientPermissionException -> HttpStatus.UNAUTHORIZED
            is ResourceAlreadyExistsException -> HttpStatus.CONFLICT
            is ResourceNotFoundException -> HttpStatus.NOT_FOUND
            else -> HttpStatus.BAD_REQUEST
        }

        val (message, error) = when(ex) {
            is UnauthorizedAccessException, is InsufficientPermissionException ->
                Pair("Unauthorized access: ${ex.message}", "Unauthorized access")
            is ResourceAlreadyExistsException ->
                Pair("Resource already exists: ${ex.message}", "Resource already exists")
            is InvalidInputException ->
                Pair("Invalid input: ${ex.message}", "Invalid input")
            is NumberFormatException ->
                Pair("Invalid number format: ${ex.message}", "Number format error")
            is ResourceNotFoundException ->
                Pair("Not found: ${ex.message}", "Resource not found")
            else ->
                Pair("Error: ${ex.message}", "Unknown error")
        }

        val apiError = createSingleErrorResponse(message, error)
        return ResponseEntity(apiError, httpStatus)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ApiError> {
        val details = ex.constraintViolations.map { violation ->
            ErrorDetail(
                field = violation.propertyPath.toString(),
                error = violation.message
            )
        }

        val apiError = ApiError(
            message = "Validation failed",
            details = details
        )

        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleInvalidTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiError> {
        val apiError = createSingleErrorResponse(
            message = "Invalid value for path variable",
            error = "Type mismatch: expected ${ex.requiredType?.simpleName}",
            field = ex.name
        )

        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [
        JsonMappingException::class,
        JsonParseException::class,
        MismatchedInputException::class
    ])
    fun handleJacksonExceptions(ex: Exception): ResponseEntity<ApiError> {
        val details = mutableListOf<ErrorDetail>()

        when (ex) {
            is MismatchedInputException -> {
                val path = ex.path
                if (path.isNotEmpty()) {
                    val fieldName = path.last().fieldName ?: "[${path.last().index}]"
                    details.add(ErrorDetail(
                        field = fieldName,
                        error = "Invalid or missing field value for '$fieldName'"
                    ))
                } else {
                    details.add(ErrorDetail(
                        field = null,
                        error = "Invalid input format: ${ex.originalMessage}"
                    ))
                }
            }
            is JsonMappingException -> {
                ex.path.forEach { pathElement ->
                    val fieldName = pathElement.fieldName ?: "[${pathElement.index}]"
                    details.add(ErrorDetail(
                        field = fieldName,
                        error = "Invalid data format at '$fieldName': ${ex.originalMessage}"
                    ))
                }
            }
            is JsonParseException -> {
                details.add(ErrorDetail(
                    field = "Unknown",
                    error = "Invalid JSON format: ${ex.message}"
                ))
            }
            else -> {
                details.add(ErrorDetail(
                    field = "Unknown",
                    error = "Data format error: ${ex.message}"
                ))
            }
        }

        val apiError = ApiError(
            message = "Data format error",
            details = details
        )

        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllOtherExceptions(ex: Exception): ResponseEntity<ApiError> {
        val apiError = createSingleErrorResponse(
            message = "An unexpected error occurred",
            error = ex.javaClass.simpleName
        )

        return ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun createSingleErrorResponse(message: String, error: String, field: String? = null): ApiError {
        return ApiError(
            message = message,
            details = listOf(ErrorDetail(field = field, error = error))
        )
    }
}