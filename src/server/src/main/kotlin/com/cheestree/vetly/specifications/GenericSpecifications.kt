package com.cheestree.vetly.specifications

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class GenericSpecifications {
    companion object {
        fun <T> withFilters(vararg predicates: (Root<T>, CriteriaBuilder) -> Predicate?): Specification<T> {
            return Specification { root, _, cb ->
                val builtPredicates = predicates.mapNotNull { it(root, cb) }
                cb.and(*builtPredicates.toTypedArray())
            }
        }
    }
}