package com.cheestree.vetly.http.model.output.user

data class UserAuthenticated(
    val token: String,
    val user: UserInformation,
)
