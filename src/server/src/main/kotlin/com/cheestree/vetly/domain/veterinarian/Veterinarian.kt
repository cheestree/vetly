package com.cheestree.vetly.domain.veterinarian

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.enums.Role
import com.cheestree.vetly.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "veterinarian", schema = "vetly")
class Veterinarian(
    id: Long = 0,
    uid: String? = null,
    name: String,
    email: String,
    imageUrl: String? = null,
    role: Role,

    @Column(nullable = false)
    val nRegister: String,

    @ManyToMany
    @JoinTable(
        name = "part_of",
        schema = "vetly",
        joinColumns = [JoinColumn(name = "veterinarian_id")],
        inverseJoinColumns = [JoinColumn(name = "clinic_id")]
    )
    val clinics: MutableList<Clinic> = mutableListOf()
) : User(
    id = id,
    uid = uid,
    name = name,
    email = email,
    imageUrl = imageUrl,
    role = role
)