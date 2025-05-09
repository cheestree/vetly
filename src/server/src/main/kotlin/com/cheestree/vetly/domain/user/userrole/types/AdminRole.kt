package com.cheestree.vetly.domain.user.userrole.types

import com.cheestree.vetly.domain.user.roles.Role.ADMIN
import com.cheestree.vetly.domain.user.roles.RoleEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "admins", schema = "vetly")
class AdminRole(
    id: Long = 0,
    name: String,
) : RoleEntity(id, ADMIN)
