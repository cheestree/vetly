package com.cheestree.vetly.http.api

import com.cheestree.vetly.domain.annotation.HiddenUser
import com.cheestree.vetly.domain.error.ApiError
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
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

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
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request body",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @PostMapping(LOGIN)
    fun login(
        @RequestBody input: UserLoginInputModel,
        response: HttpServletResponse,
    ): ResponseEntity<UserAuthenticated>

    @Operation(
        summary = "Logs out the authenticated user",
        security = [SecurityRequirement(name = "bearerAuth")]
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
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                description = "User not authenticated",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
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
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET)
    fun getUserProfile(
        @PathVariable userId: UUID,
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
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping(GET_USER_PROFILE)
    fun getMyProfile(
        @HiddenUser authenticatedUser: AuthenticatedUser,
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
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiError::class),
                    ),
                ],
            ),
        ],
    )
    @PutMapping(UPDATE_USER_PROFILE)
    fun updateMyProfile(
        @HiddenUser authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid input: UserUpdateInputModel,
    ): ResponseEntity<UserInformation>
}
