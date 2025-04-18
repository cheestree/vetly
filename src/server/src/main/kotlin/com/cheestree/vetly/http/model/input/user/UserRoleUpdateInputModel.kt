package com.cheestree.vetly.http.model.input.user

import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.model.input.request.RequestExtraData

data class UserRoleUpdateInputModel(
    val roleName: Role,
    val permissions: List<String>
) : RequestExtraData