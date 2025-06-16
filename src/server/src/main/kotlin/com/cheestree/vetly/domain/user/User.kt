package com.cheestree.vetly.domain.user

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.animal.Animal
import com.cheestree.vetly.domain.clinic.ClinicMembership
import com.cheestree.vetly.domain.guide.Guide
import com.cheestree.vetly.domain.request.Request
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.http.model.output.user.UserLink
import com.cheestree.vetly.http.model.output.user.UserPreview
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users", schema = "vetly")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true, updatable = false)
    val publicId: UUID = UUID.randomUUID(),
    @Column(nullable = true, unique = true)
    val uid: String? = null,
    var username: String,
    var email: String,
    var imageUrl: String? = null,
    var phone: String? = null,
    var birthDate: Date? = null,
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    val roles: MutableSet<UserRole> = mutableSetOf(),
    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL], orphanRemoval = true)
    val animals: MutableSet<Animal> = mutableSetOf(),
    @OneToMany(mappedBy = "veterinarian", cascade = [CascadeType.ALL], orphanRemoval = true)
    val clinicMemberships: MutableSet<ClinicMembership> = mutableSetOf(),
    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    val guides: MutableSet<Guide> = mutableSetOf(),
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val requests: MutableSet<Request> = mutableSetOf(),
) : BaseEntity() {
    fun addRole(role: UserRole) {
        roles.add(role)
    }

    fun removeRole(role: UserRole) {
        roles.removeIf { it.id == role.id }
    }

    fun addGuide(guide: Guide) {
        guides.add(guide)
    }

    fun removeGuide(guide: Guide) {
        guides.removeIf { it.id == guide.id }
    }

    fun addAnimal(animal: Animal) {
        animals.add(animal)
    }

    fun removeAnimal(animal: Animal) {
        animals.removeIf { it.id == animal.id }
    }

    fun addRequest(request: Request) {
        requests.add(request)
    }

    fun removeRequest(request: Request) {
        requests.removeIf { it.id == request.id }
    }

    fun updateWith(
        username: String? = null,
        imageUrl: String? = null,
        phone: String? = null,
        birthDate: Date? = null,
    ) {
        username?.let { this.username = it }
        imageUrl?.let { this.imageUrl = it }
        phone?.let { this.phone = it }
        birthDate?.let { this.birthDate = it }
    }

    fun toAuthenticatedUser() =
        AuthenticatedUser(
            id = id,
            uid = uid,
            name = username,
            email = email,
            roles = roles.mapTo(mutableSetOf()) { Role.valueOf(it.role.role.name) },
        )

    fun asPublic() =
        UserInformation(
            id = publicId,
            name = username,
            email = email,
            imageUrl = imageUrl,
            roles = roles.mapTo(mutableSetOf()) { Role.valueOf(it.role.role.name) },
            clinics = clinicMemberships.map { it.asPreview() },
            joinedAt = createdAt.toLocalDate(),
        )

    fun asLink() =
        UserLink(
            id = publicId,
            name = username,
            imageUrl = imageUrl,
        )

    fun asPreview() =
        UserPreview(
            id = publicId,
            name = username,
            email = email,
            imageUrl = imageUrl,
        )
}
