package com.cheestree.vetly.domain.exception

sealed class VetException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    //  Authentication/Authorization issues
    class UnauthorizedAccessException(
        message: String,
    ) : VetException(message)

    class InactiveResourceException(
        resourceType: ResourceType,
        resourceId: Any,
    ) : VetException("$resourceType with id $resourceId is not active")

    class ForbiddenException(
        message: String,
    ) : VetException(message)

    //  Resource state issues
    class ResourceNotFoundException(
        resourceType: ResourceType,
        resourceId: Any,
    ) : VetException("$resourceType with id $resourceId not found")

    class ResourceAlreadyExistsException(
        resourceType: ResourceType,
        identifier: String,
        value: Any,
    ) : VetException("$resourceType with $identifier $value already exists")

    //  Input validation
    class ValidationException(
        message: String,
    ) : VetException(message)

    //  Operations
    class OperationFailedException(
        operation: String,
        resourceType: ResourceType? = null,
        resourceId: Any? = null,
        cause: Throwable? = null,
    ) : VetException(
        message =
            buildString {
                append("Failed to $operation")
                if (resourceType != null) append(" $resourceType")
                if (resourceId != null) append(" with id $resourceId")
            },
        cause = cause,
    )

    //  Conflicts
    class ConflictException(
        message: String,
    ) : VetException(message)

    enum class ResourceType {
        USER,
        ROLE,
        VETERINARIAN,
        REQUEST,
        CLINIC,
        CLINIC_MEMBERSHIP,
        SUPPLY,
        CHECKUP,
        GUIDE,
        ANIMAL,
        ;

        override fun toString(): String =
            name
                .trim('_')
                .lowercase()
                .replaceFirstChar { it.uppercaseChar() }
    }
}
