package com.cheestree.vetly.domain.veterinarian

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import jakarta.persistence.*

@Entity
@Table(name = "veterinarian", schema = "vetly")
@PrimaryKeyJoinColumn(name = "id")
class Veterinarian(
    id: Long = 0,
    uid: String? = null,
    name: String,
    email: String,
    imageUrl: String? = null,
    roles: List<Role>,

    @Column(nullable = false)
    val nRegister: String,

    @ManyToMany
    @JoinTable(
        name = "part_of",
        schema = "vetly",
        joinColumns = [JoinColumn(name = "veterinarian_id")],
        inverseJoinColumns = [JoinColumn(name = "clinic_id")]
    )
    val clinics: Set<Clinic> = setOf()
) : User(
    id = id,
    uid = uid,
    username = name,
    email = email,
    imageUrl = imageUrl,
    roles = roles
)