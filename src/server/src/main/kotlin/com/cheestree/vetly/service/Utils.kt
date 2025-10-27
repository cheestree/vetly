package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException
import com.cheestree.vetly.domain.exception.VetException.OperationFailedException
import com.cheestree.vetly.domain.exception.VetException.ResourceType

object Utils {
    fun <T> executeOperation(
        operation: String,
        resourceType: ResourceType? = null,
        resourceId: Any? = null,
        block: () -> T,
    ): T =
        try {
            block()
        } catch (e: Exception) {
            when (e) {
                is VetException -> throw e
                else -> throw OperationFailedException(operation, resourceType, resourceId, e)
            }
        }

    fun <T> retrieveResource(
        resourceType: ResourceType,
        resourceId: Any,
        block: () -> T,
    ): T = executeOperation("retrieve", resourceType, resourceId, block)

    fun <T> createResource(
        resourceType: ResourceType,
        block: () -> T,
    ): T = executeOperation("create", resourceType, null, block)

    fun <T> updateResource(
        resourceType: ResourceType,
        resourceId: Any,
        block: () -> T,
    ): T = executeOperation("update", resourceType, resourceId, block)

    fun <T> deleteResource(
        resourceType: ResourceType,
        resourceId: Any,
        block: () -> T,
    ): T = executeOperation("delete", resourceType, resourceId, block)
}
