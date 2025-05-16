package com.cheestree.vetly.specification

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class GenericSpecifications {
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
    }
}
