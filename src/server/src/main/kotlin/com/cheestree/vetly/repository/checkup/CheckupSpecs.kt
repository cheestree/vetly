package com.cheestree.vetly.repository.checkup

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.checkup.Checkup
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.repository.BaseSpecs
import jakarta.persistence.criteria.Join
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object CheckupSpecs {
    fun ownCheckup(
        roles: Set<Role>,
        userId: Long,
    ) = Specification<Checkup> { root, _, cb ->
        when {
            Role.ADMIN in roles -> null

            Role.VETERINARIAN in roles -> {
                val vetJoin: Join<Checkup, User> = root.join("veterinarian")
                cb.equal(vetJoin.get<Long>("id"), userId)
            }

            else -> {
                val animalJoin: Join<Checkup, Animal> = root.join("animal")
                val ownerJoin: Join<Animal, User> = animalJoin.join("owner")
                cb.equal(ownerJoin.get<Long>("id"), userId)
            }
        }
    }

    fun titleContains(title: String?) = BaseSpecs.likeString<Checkup>(title, "title")

    fun createdAt(
        from: LocalDate?,
        to: LocalDate?,
    ) = BaseSpecs.betweenDates<Checkup>(from, to, "createdAt")

    fun veterinarianEquals(vetId: Long?) = BaseSpecs.equalObjectLong<Checkup>(vetId, "veterinarian", "id")

    fun veterinarianUsernameEquals(username: String?) = BaseSpecs.likeObjectString<Checkup>(username, "veterinarian", "username")

    fun animalEquals(animalId: Long?) = BaseSpecs.equalObjectLong<Checkup>(animalId, "animal", "id")

    fun animalNameEquals(animalName: String?) = BaseSpecs.likeObjectString<Checkup>(animalName, "animal", "name")

    fun clinicEquals(clinicId: Long?) = BaseSpecs.equalObjectLong<Checkup>(clinicId, "clinic", "id")

    fun clinicNameEquals(clinicName: String?) = BaseSpecs.likeObjectString<Checkup>(clinicName, "clinic", "name")
}
