package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException
import com.cheestree.vetly.domain.exception.VetException.OperationFailedException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.filter.Filter
import com.cheestree.vetly.domain.filter.Operation
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.util.*

class Utils {
    companion object {
        fun <T> combineAll(vararg specs: Specification<T>?): Specification<T>? =
            specs.filterNotNull().reduceOrNull { acc, spec -> acc.and(spec) }

        fun <T> withFilters(vararg predicates: (Root<T>, CriteriaBuilder) -> Predicate?): Specification<T> =
            Specification { root, _, cb ->
                val builtPredicates = predicates.mapNotNull { it(root, cb) }
                cb.and(*builtPredicates.toTypedArray())
            }

        @Suppress("UNCHECKED_CAST")
        fun <T> mappedFilters(filters: List<Filter<*>>): Specification<T>? {
            val specs =
                filters.mapNotNull { filter ->
                    val value = filter.value ?: return@mapNotNull null
                    Specification<T> { root, _, cb ->
                        val path = resolveObjectPath(root, filter.path)

                        when (filter.operation) {
                            Operation.EQUAL -> cb.equal(path, value)
                            Operation.LIKE -> {
                                val pattern = "%${value.toString().lowercase(Locale.getDefault())}%"
                                if (filter.caseInsensitive) {
                                    cb.like(cb.lower(path.`as`(String::class.java)), pattern)
                                } else {
                                    cb.like(path.`as`(String::class.java), pattern)
                                }
                            }

                            Operation.GREATER_THAN ->
                                cb.greaterThan(
                                    path as Path<Comparable<Any>>,
                                    value as Comparable<Any>,
                                )

                            Operation.LESS_THAN -> cb.lessThan(path as Path<Comparable<Any>>, value as Comparable<Any>)
                            Operation.IS_TRUE -> cb.isTrue(path as Path<Boolean>)
                            Operation.IS_FALSE -> cb.isFalse(path as Path<Boolean>)
                            Operation.BETWEEN -> {
                                val range = value as? Pair<*, *>
                                val start = range?.first as? Comparable<Any>
                                val end = range?.second as? Comparable<Any>
                                val cmpPath = path as Path<Comparable<Any>>

                                when {
                                    start != null && end != null -> cb.between(cmpPath, start, end)
                                    start != null -> cb.greaterThanOrEqualTo(cmpPath, start)
                                    end != null -> cb.lessThanOrEqualTo(cmpPath, end)
                                    else -> cb.conjunction()
                                }
                            }
                        }
                    }
                }
            return specs.reduceOrNull { acc, spec -> acc.and(spec) }
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T> resolveObjectPath(
            root: Path<T>,
            field: String,
        ): Path<Any> {
            val parts = field.split(".")
            var path: Path<*> = root
            for (part in parts) {
                path = path.get<Any>(part)
            }
            return path as Path<Any>
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
