package com.cheestree.vetly.domain.exception

sealed class VetException(message: String) : Exception(message) {
    class UnauthorizedAccessException(message: String) : VetException(message)
    class InsufficientPermissionException(message: String) : VetException(message)
    class ResourceNotFoundException(message: String) : VetException(message)
    class ResourceAlreadyExistsException(message: String) : VetException(message)
    class InvalidInputException(message: String) : VetException(message)
    class InsufficientCharacterException(message: String) : VetException(message)
    class BadRequestException(message: String) : VetException(message)
}