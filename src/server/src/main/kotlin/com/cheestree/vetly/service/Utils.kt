package com.cheestree.vetly.service

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.exception.VetException
import com.cheestree.vetly.domain.exception.VetException.OperationFailedException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class Utils {
    companion object {
        fun <T> withFilters(vararg predicates: (Root<T>, CriteriaBuilder) -> Predicate?): Specification<T> =
            Specification { root, _, cb ->
                val builtPredicates = predicates.mapNotNull { it(root, cb) }
                cb.and(*builtPredicates.toTypedArray())
            }

        fun checkupOwnershipFilter(
            roles: Set<Role>,
            id: Long,
        ): Specification<Checkup> =
            Specification { root, _, cb ->
                when {
                    Role.ADMIN in roles -> null

                    Role.VETERINARIAN in roles -> {
                        val vetJoin: Join<Checkup, User> = root.join("veterinarian")
                        cb.equal(vetJoin.get<Long>("id"), id)
                    }

                    else -> {
                        val animalJoin: Join<Checkup, Animal> = root.join("animal")
                        val ownerJoin: Join<Animal, User> = animalJoin.join("owner")
                        cb.equal(ownerJoin.get<Long>("id"), id)
                    }
                }
            }

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
}
