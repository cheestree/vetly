package com.cheestree.vetly.domain.user.userrole

import jakarta.persistence.Embeddable

@Embeddable
data class UserRoleId(
    val userId: Long,
    val roleId: Long
)