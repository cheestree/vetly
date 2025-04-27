package com.cheestree.vetly.domain.user.userrole.types

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.user.roles.Role.VETERINARIAN
import com.cheestree.vetly.domain.user.roles.RoleEntity
import jakarta.persistence.*

@Entity
@Table(name = "veterinarian", schema = "vetly")
class VeterinarianRole(
    id: Long = 0,
    name: String,

    @Column(nullable = true)
    val nRegister: String? = null,
) : RoleEntity(id, VETERINARIAN)