package com.cheestree.vetly.domain.user

import com.cheestree.vetly.domain.enums.Role
import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users", schema = "vetly")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, updatable = false)
    val uuid: UUID = UUID.randomUUID(),

    @Column(nullable = true, unique = true)
    val uid: String? = null,

    val name: String,
    val email: String,
    val imageUrl: String? = null,
    val phone: Int? = null,
    val birth: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    val role: Role
) {
    fun toAuthenticatedUser() = AuthenticatedUser(
        id = id,
        uid = uid,
        name = name,
        email = email,
        role = role
    )
    fun toUserProfile() = UserProfile(
        id = id,
        name = name,
        email = email,
        imageUrl = imageUrl,
        role = role.name
    )
}