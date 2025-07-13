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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(
        value = [
            VetException.ValidationException::class,
            MethodArgumentNotValidException::class,
            ConstraintViolationException::class,
            MethodArgumentTypeMismatchException::class,
            JsonMappingException::class,
            JsonParseException::class,
            MismatchedInputException::class,
            HttpMessageNotReadableException::class,
        ],
    )
    fun handleBadRequest(ex: Exception): ResponseEntity<ApiError> {
        val body =
            when (ex) {
                is VetException.ValidationException ->
                    createSingleErrorResponse("Invalid input: ${ex.message}", "Invalid input")

                is MethodArgumentNotValidException -> {
                    val details =
                        ex.bindingResult.fieldErrors.map {
                            ErrorDetail(it.field, it.defaultMessage ?: "Validation failed")
                        }
                    ApiError("Validation failed", details)
                }

                is ConstraintViolationException -> {
                    val details =
                        ex.constraintViolations.map {
                            ErrorDetail(it.propertyPath.toString(), it.message)
                        }
                    ApiError("Validation failed", details)
                }

                is MethodArgumentTypeMismatchException ->
                    createSingleErrorResponse(
                        "Invalid value for path variable",
                        "Type mismatch: expected ${ex.requiredType?.simpleName}",
                        ex.name,
                    )

                is MismatchedInputException -> {
                    val details =
                        if (ex.path.isNotEmpty()) {
                            val field = ex.path.last().fieldName ?: "[${ex.path.last().index}]"
                            listOf(ErrorDetail(field, "Invalid or missing field value for '$field'"))
                        } else {
                            listOf(ErrorDetail(null, "Invalid input format: ${ex.originalMessage}"))
                        }
                    ApiError("Data format error", details)
                }

                is JsonMappingException -> {
                    val details =
                        ex.path.map {
                            val field = it.fieldName ?: "[${it.index}]"
                            ErrorDetail(field, "Invalid data format at '$field': ${ex.originalMessage}")
                        }
                    ApiError("Data format error", details)
                }

                is JsonParseException ->
                    ApiError(
                        "Data format error",
                        listOf(ErrorDetail("Unknown", "Invalid JSON format: ${ex.message}")),
                    )

                is HttpMessageNotReadableException ->
                    createSingleErrorResponse("An unexpected error occurred", "Invalid or missing request body.")

                else -> createSingleErrorResponse("Bad request", ex.message ?: "Unknown error")
            }

        return errorResponse(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
        value = [
            VetException.ForbiddenException::class,
            VetException.InactiveResourceException::class,
        ],
    )
    fun handleForbidden(ex: VetException): ResponseEntity<ApiError> =
        errorResponse(
            createSingleErrorResponse("Forbidden: ${ex.message}", "Forbidden access"),
            HttpStatus.FORBIDDEN,
        )

    @ExceptionHandler(VetException.ResourceNotFoundException::class)
    fun handleNotFound(ex: VetException.ResourceNotFoundException): ResponseEntity<ApiError> =
        errorResponse(
            createSingleErrorResponse("Not found: ${ex.message}", "Resource not found"),
            HttpStatus.NOT_FOUND,
        )

    @ExceptionHandler(
        value = [
            VetException.ConflictException::class,
            VetException.ResourceAlreadyExistsException::class,
        ],
    )
    fun handleConflict(ex: VetException): ResponseEntity<ApiError> {
        val type =
            if (ex is VetException.ResourceAlreadyExistsException) {
                "Resource already exists"
            } else {
                "Conflict"
            }

        return errorResponse(
            createSingleErrorResponse("$type: ${ex.message}", type),
            HttpStatus.CONFLICT,
        )
    }

    @ExceptionHandler(VetException.UnauthorizedAccessException::class)
    fun handleUnauthorized(ex: VetException.UnauthorizedAccessException): ResponseEntity<ApiError> =
        errorResponse(
            createSingleErrorResponse("Unauthorized access: ${ex.message}", "Unauthorized access"),
            HttpStatus.UNAUTHORIZED,
        )

    @ExceptionHandler(
        value = [
            VetException.OperationFailedException::class,
            Exception::class,
        ],
    )
    fun handleInternalServerErrors(ex: Exception): ResponseEntity<ApiError> {
        logger.error("Unexpected error occurred", ex)

        val (message, type) =
            when (ex) {
                is VetException.OperationFailedException -> "Internal error: ${ex.message}" to "Operation failed"
                else -> "An unexpected error occurred" to ex.javaClass.simpleName
            }

        return errorResponse(
            createSingleErrorResponse(message, type),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    private fun errorResponse(
        error: ApiError,
        status: HttpStatus,
    ): ResponseEntity<ApiError> =
        ResponseEntity
            .status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(error)

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
