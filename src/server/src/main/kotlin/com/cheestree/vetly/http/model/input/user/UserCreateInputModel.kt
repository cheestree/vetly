package com.cheestree.vetly.http.model.input.user

import com.cheestree.vetly.domain.annotation.ValidUsername
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class UserCreateInputModel(
    @field:NotBlank
    val uid: String,

    @field:ValidUsername
    val username: String,

    @field:NotEmpty
    @field:Email
    val email: String,

    val profileUrl: String? = null,
)