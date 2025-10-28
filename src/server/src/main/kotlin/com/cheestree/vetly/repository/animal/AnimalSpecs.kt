package com.cheestree.vetly.repository.animal

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.repository.BaseSpecs
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object AnimalSpecs {
    fun nameContains(name: String?) = BaseSpecs.likeString<Animal>(name, "name")

    fun microchipEquals(microchip: String?) = BaseSpecs.likeString<Animal>(microchip, "microchip")

    fun sexEquals(sex: Sex?) =
        Specification<Animal> { root, _, cb ->
            sex?.let { cb.equal(root.get<Sex>("sex"), it) } ?: cb.conjunction()
        }

    fun isSterile(sterile: Boolean?) =
        Specification<Animal> { root, _, cb ->
            sterile?.let { cb.equal(root.get<Boolean>("sterilized"), it) } ?: cb.conjunction()
        }

    fun speciesEquals(species: String?) = BaseSpecs.likeString<Animal>(species, "species")

    fun bornIn(
        from: LocalDate?,
        to: LocalDate?,
    ) = BaseSpecs.betweenDates<Animal>(
        from,
        to,
        "birthDate",
    )

    fun hasOwner(owned: Boolean?) =
        Specification<Animal> { root, _, cb ->
            owned?.let {
                if (it) {
                    cb.isNotNull(root.get<User>("owner"))
                } else {
                    cb.isNull(root.get<User>("owner"))
                }
            } ?: cb.conjunction()
        }

    fun isSelf(
        self: Boolean?,
        userId: Long,
    ) = Specification<Animal> { root, _, cb ->
        self?.let {
            val ownerId = root.get<User>("owner").get<Long>("id")
            if (it) cb.equal(ownerId, userId) else cb.notEqual(ownerId, userId)
        } ?: cb.conjunction()
    }

    fun byEmail(
        userEmail: String?,
        authRoles: Set<Role>,
        authEmail: String,
    ) = Specification<Animal> { root, _, cb ->
        val ownerEmail = root.get<User>("owner").get<String>("email")
        val isAdmin = authRoles.contains(Role.ADMIN)
        val isVet = authRoles.contains(Role.VETERINARIAN)

        when {
            !isAdmin && !isVet -> cb.like(ownerEmail, "%$authEmail%")
            userEmail != null -> cb.like(ownerEmail, "%$userEmail%")
            else -> cb.conjunction()
        }
    }

    fun isActive(
        active: Boolean?,
        authRoles: Set<Role>,
    ) = Specification<Animal> { root, _, cb ->
        active?.takeIf { authRoles.any { it in setOf(Role.ADMIN, Role.VETERINARIAN) } }?.let {
            cb.equal(root.get<Boolean>("isActive"), it)
        } ?: cb.conjunction()
    }
}
