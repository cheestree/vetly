package com.cheestree.vetly.domain.user.userrole

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class UserRoleId(
    val userId: Long,
    val roleId: Long,
) : Serializable
