package com.cheestree.vetly.domain.user.userrole

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.RoleEntity
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_roles", schema = "vetly")
class UserRole(
    @EmbeddedId
    val id: UserRoleId,
    @ManyToOne
    @MapsId("userId")
    val user: User,
    @ManyToOne
    @MapsId("roleId")
    val role: RoleEntity,
) : BaseEntity() {
    override fun toString(): String = this.role.role.name
}
