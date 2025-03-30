package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.user.RoleRequestInputModel
import com.cheestree.vetly.http.model.input.user.UserLoginInputModel
import com.cheestree.vetly.http.model.output.user.RoleRequestInformation
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.http.path.Path.Users.GET
import com.cheestree.vetly.http.path.Path.Users.LOGIN
import com.cheestree.vetly.http.path.Path.Users.LOGOUT
import com.cheestree.vetly.http.path.Path.Users.REQUEST_ROLE
import com.cheestree.vetly.service.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService
) {
    @GetMapping(GET)
    @AuthenticatedRoute
    fun getUserProfile(
        @PathVariable userId: Long
    ): ResponseEntity<UserInformation> {
        return ResponseEntity.ok(userService.getUserById(userId))
    }

    @PostMapping(LOGIN)
    fun login(
        response: HttpServletResponse,
        @RequestBody @Valid login: UserLoginInputModel
    ): ResponseEntity<AuthenticatedUser> {
        return ResponseEntity.ok(userService.login(login.idToken)) // Firebase ID token verification
    }

    @GetMapping(LOGOUT)
    @AuthenticatedRoute
    fun logout(): String {
        // Handled by interceptor
        return "logout"
    }

    @PostMapping(REQUEST_ROLE)
    @AuthenticatedRoute
    fun requestRole(
        authenticatedUser: AuthenticatedUser,
        @RequestBody @Valid request: RoleRequestInputModel
    ): ResponseEntity<RoleRequestInformation> {
        return ResponseEntity.ok(userService.requestRole(
            authenticatedUser.id,
            request.requestedRole,
            request.justification,
            request.fileUrl
        ))
    }
}