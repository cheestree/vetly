package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.user.UserLoginInputModel
import com.cheestree.vetly.http.model.input.user.UserUpdateInputModel
import com.cheestree.vetly.http.model.output.user.UserAuthenticated
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.http.path.Path.Users.GET
import com.cheestree.vetly.http.path.Path.Users.GET_USER_PROFILE
import com.cheestree.vetly.http.path.Path.Users.LOGIN
import com.cheestree.vetly.http.path.Path.Users.LOGOUT
import com.cheestree.vetly.http.path.Path.Users.UPDATE_USER_PROFILE
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "User")
interface UserApi {
    @Operation(
        summary = "Authenticates a user and sets authentication cookie",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User authenticated successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserAuthenticated::class),
                    ),
                ],
                headers = [
                    Header(
                        name = "Set-Cookie",
                        description = "Authentication token cookie",
                        schema = Schema(type = "string"),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(LOGIN)
    fun login(
        @RequestBody loggedUser: UserLoginInputModel,
        response: HttpServletResponse,
    ): ResponseEntity<UserAuthenticated>

    @Operation(
        summary = "Logs out the authenticated user",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User logged out successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = String::class),
                    ),
                ],
                headers = [
                    Header(
                        name = "Set-Cookie",
                        description = "Expired authentication cookie",
                        schema = Schema(type = "string"),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(LOGOUT)
    fun logout(request: HttpServletRequest): ResponseEntity<*>

    @Operation(
        summary = "Fetches a users' profile",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User profile fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserInformation::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET)
    fun getUserProfile(
        @PathVariable id: UUID,
    ): ResponseEntity<UserInformation>

    @Operation(
        summary = "Fetches the users' profile",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User profile fetched successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserInformation::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_USER_PROFILE)
    fun getMyProfile(
        @HiddenUser user: AuthenticatedUser,
    ): ResponseEntity<UserInformation>

    @Operation(
        summary = "Updates the users' profile",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User profile updated successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = UserInformation::class),
                    ),
                ],
            ),
        ],
    )
    @PatchMapping(UPDATE_USER_PROFILE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateMyProfile(
        @HiddenUser user: AuthenticatedUser,
        @RequestPart("user") @Valid updatedUser: UserUpdateInputModel,
    ): ResponseEntity<UserInformation>
}
