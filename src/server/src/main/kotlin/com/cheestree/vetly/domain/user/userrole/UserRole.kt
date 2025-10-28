package com.cheestree.vetly.domain.user.userrole

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.RoleEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.OneToOne
import jakarta.persistence.CascadeType
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn

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
