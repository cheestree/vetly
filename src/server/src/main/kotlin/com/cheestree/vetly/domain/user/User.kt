package com.cheestree.vetly.domain.user

import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.clinic.ClinicMembership
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.http.model.output.user.UserPreview
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users", schema = "vetly")
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, updatable = false)
    val uuid: UUID = UUID.randomUUID(),

    @Column(nullable = true, unique = true)
    val uid: String? = null,

    val username: String,
    val email: String,
    val imageUrl: String? = null,
    val phone: Int? = null,
    val birthDate: OffsetDateTime? = null,

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    val roles: MutableSet<UserRole> = mutableSetOf(),

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], orphanRemoval = true)
    val animals: MutableSet<Animal> = mutableSetOf(),

    @OneToMany(mappedBy = "veterinarian", cascade = [CascadeType.ALL], orphanRemoval = true)
    val clinicMemberships: MutableSet<ClinicMembership> = mutableSetOf(),

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    val guides: MutableSet<Guide> = mutableSetOf(),
) {
    fun addRole(role: UserRole) {
        roles.add(role)
    }
    fun addGuide(guide: Guide) {
        this.guides.add(guide)
    }
    fun updateGuideList(guide: Guide) {
        if (!this.guides.contains(guide)) {
            this.guides.add(guide)
        }
    }
    fun removeGuide(guide: Guide) {
        this.guides.remove(guide)
    }
    fun addAnimal(animal: Animal) {
        this.animals.add(animal)
    }

    fun removeAnimal(animal: Animal) {
        this.animals.remove(animal)
    }

    fun toAuthenticatedUser() = AuthenticatedUser(
        id = id,
        uid = uid,
        name = username,
        email = email,
        roles = roles.mapTo(mutableSetOf()) { Role.valueOf(it.role.role.name) },
    )
    fun asPublic() = UserInformation(
        id = id,
        name = username,
        email = email,
        imageUrl = imageUrl,
        roles = roles.mapTo(mutableSetOf()) { Role.valueOf(it.role.role.name) },
    )
    fun asPreview() = UserPreview(
        id = id,
        name = username,
        imageUrl = imageUrl
    )
}