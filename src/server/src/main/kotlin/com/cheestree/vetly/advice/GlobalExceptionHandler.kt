package com.cheestree.vetly.advice

import com.cheestree.vetly.domain.error.ApiError
import com.cheestree.vetly.domain.error.ErrorDetail
import com.cheestree.vetly.domain.exception.VetException.*
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

    private fun createSingleErrorResponse(message: String, error: String, field: String? = null): ApiError {
        return ApiError(
            message = message,
            details = listOf(ErrorDetail(field = field, error = error))
        )
    }
}