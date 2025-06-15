package com.cheestree.vetly.http

import com.cheestree.vetly.domain.error.ApiError
import com.cheestree.vetly.domain.error.ErrorDetail
import com.cheestree.vetly.domain.exception.VetException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(VetException::class)
    fun handleVetExceptions(ex: VetException): ResponseEntity<ApiError> {
        val httpStatus =
            when (ex) {
                is VetException.UnauthorizedAccessException -> HttpStatus.UNAUTHORIZED
                is VetException.InactiveResourceException -> HttpStatus.FORBIDDEN
                is VetException.ForbiddenException -> HttpStatus.FORBIDDEN
                is VetException.ResourceNotFoundException -> HttpStatus.NOT_FOUND
                is VetException.ResourceAlreadyExistsException -> HttpStatus.CONFLICT
                is VetException.ValidationException -> HttpStatus.BAD_REQUEST
                is VetException.ConflictException -> HttpStatus.CONFLICT
                is VetException.OperationFailedException -> {
                    logger.error("Operation failed", ex)
                    HttpStatus.INTERNAL_SERVER_ERROR
                }
            }

        val errorMessage =
            when (ex) {
                is VetException.UnauthorizedAccessException -> "Unauthorized access: ${ex.message}"
                is VetException.InactiveResourceException -> "Forbidden: ${ex.message}"
                is VetException.ResourceNotFoundException -> "Not found: ${ex.message}"
                is VetException.ResourceAlreadyExistsException -> "Resource already exists: ${ex.message}"
                is VetException.ValidationException -> "Invalid input: ${ex.message}"
                is VetException.ConflictException -> "Conflict: ${ex.message}"
                is VetException.OperationFailedException -> "Internal error: ${ex.message}"
                is VetException.ForbiddenException -> "Forbidden: ${ex.message}"
            }

        val errorType =
            when (ex) {
                is VetException.UnauthorizedAccessException -> "Unauthorized access"
                is VetException.InactiveResourceException -> "Resource inactive"
                is VetException.ResourceNotFoundException -> "Resource not found"
                is VetException.ResourceAlreadyExistsException -> "Resource already exists"
                is VetException.ValidationException -> "Invalid input"
                is VetException.ConflictException -> "Conflict"
                is VetException.OperationFailedException -> "Operation failed"
                is VetException.ForbiddenException -> "Forbidden access"
            }

        val apiError = createSingleErrorResponse(errorMessage, errorType)
        return ResponseEntity(apiError, httpStatus)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ApiError> {
        val details =
            ex.constraintViolations.map { violation ->
                ErrorDetail(
                    field = violation.propertyPath.toString(),
                    error = violation.message,
                )
            }

        val apiError =
            ApiError(
                message = "Validation failed",
                details = details,
            )

        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleInvalidTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiError> {
        val apiError =
            createSingleErrorResponse(
                message = "Invalid value for path variable",
                error = "Type mismatch: expected ${ex.requiredType?.simpleName}",
                field = ex.name,
            )

        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
        value = [
            JsonMappingException::class,
            JsonParseException::class,
            MismatchedInputException::class,
        ],
    )
    fun handleJacksonExceptions(ex: Exception): ResponseEntity<ApiError> {
        val details = mutableListOf<ErrorDetail>()

        when (ex) {
            is MismatchedInputException -> {
                val path = ex.path
                if (path.isNotEmpty()) {
                    val fieldName = path.last().fieldName ?: "[${path.last().index}]"
                    details.add(
                        ErrorDetail(
                            field = fieldName,
                            error = "Invalid or missing field value for '$fieldName'",
                        ),
                    )
                } else {
                    details.add(
                        ErrorDetail(
                            field = null,
                            error = "Invalid input format: ${ex.originalMessage}",
                        ),
                    )
                }
            }
            is JsonMappingException -> {
                ex.path.forEach { pathElement ->
                    val fieldName = pathElement.fieldName ?: "[${pathElement.index}]"
                    details.add(
                        ErrorDetail(
                            field = fieldName,
                            error = "Invalid data format at '$fieldName': ${ex.originalMessage}",
                        ),
                    )
                }
            }
            is JsonParseException -> {
                details.add(
                    ErrorDetail(
                        field = "Unknown",
                        error = "Invalid JSON format: ${ex.message}",
                    ),
                )
            }
            else -> {
                details.add(
                    ErrorDetail(
                        field = "Unknown",
                        error = "Data format error: ${ex.message}",
                    ),
                )
            }
        }

        val apiError =
            ApiError(
                message = "Data format error",
                details = details,
            )

        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NumberFormatException::class)
    fun handleNumberFormatException(ex: NumberFormatException): ResponseEntity<ApiError> {
        logger.debug("Number format exception", ex)
        val vetException = VetException.ValidationException("Invalid number format: ${ex.message}")
        return handleVetExceptions(vetException)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException?): ResponseEntity<ApiError> {
        val apiError =
            createSingleErrorResponse(
                message = "An unexpected error occurred",
                error = "Invalid or missing request body.",
            )
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllOtherExceptions(ex: Exception): ResponseEntity<ApiError> {
        logger.error("Unexpected error occurred", ex)

        val apiError =
            createSingleErrorResponse(
                message = "An unexpected error occurred",
                error = ex.javaClass.simpleName,
            )
        return ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun createSingleErrorResponse(
        message: String,
        error: String,
        field: String? = null,
    ): ApiError =
        ApiError(
            message = message,
            details = listOf(ErrorDetail(field = field, error = error)),
        )
}
