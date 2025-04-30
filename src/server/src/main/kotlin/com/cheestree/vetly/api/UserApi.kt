package com.cheestree.vetly.api

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.user.UserUpdateInputModel
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.http.path.Path.Users.GET
import com.cheestree.vetly.http.path.Path.Users.GET_USER_PROFILE
import com.cheestree.vetly.http.path.Path.Users.UPDATE_USER_PROFILE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "User")
interface UserApi {
    @Operation(
        summary = "Fetches a users' profile",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET)
    @AuthenticatedRoute
    fun getUserProfile(
        @PathVariable userId: UUID,
    ): ResponseEntity<UserInformation>

    @Operation(
        summary = "Fetches the users' profile",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @GetMapping(GET_USER_PROFILE)
    @AuthenticatedRoute
    fun getMyProfile(
        @HiddenUser authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<UserInformation>

    @Operation(
        summary = "Updates the users' profile",
        description = "Requires veterinarian role",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @PutMapping(UPDATE_USER_PROFILE)
    @AuthenticatedRoute
    fun updateMyProfile(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid input: UserUpdateInputModel,
    ): ResponseEntity<UserInformation>
}
