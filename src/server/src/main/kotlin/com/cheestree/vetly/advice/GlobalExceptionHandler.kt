package com.cheestree.vetly.advice

import com.cheestree.vetly.domain.error.Error
import com.cheestree.vetly.domain.exception.VetException.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(value = [
        UnauthorizedAccessException::class,
        InsufficientPermissionException::class
    ])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorizedAccess(ex: UnauthorizedAccessException): ResponseEntity<Error> {
        val errorResponse = Error(
            "Unauthorized access: ${ex.message}",
            "Unauthorized access",
            HttpStatus.UNAUTHORIZED
        )

        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Error> {
        val errorResponse = Error(
            "Invalid value for path variable: ${ex.name}",
            "Type mismatch",
            HttpStatus.BAD_REQUEST
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidInputException(ex: InvalidInputException): ResponseEntity<Error> {
        val errorResponse = Error(
            "Invalid input: ${ex.message}",
            "Invalid input",
            HttpStatus.BAD_REQUEST
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NumberFormatException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNumberFormatException(ex: NumberFormatException): ResponseEntity<Error> {
        val errorResponse = Error(
            "Invalid number format: ${ex.message}",
            "Number format error",
            HttpStatus.BAD_REQUEST
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<Error> {
        val errorResponse = Error(
            "Not found: ${ex.message}",
            "Resource not found",
            HttpStatus.NOT_FOUND
        )

        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }
}