package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<RoleEntity, Long> {
    fun findByRole(role: Role): Optional<RoleEntity>
}
