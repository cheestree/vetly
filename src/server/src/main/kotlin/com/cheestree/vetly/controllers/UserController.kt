package com.cheestree.vetly.controllers

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.enums.Role.MEMBER
import com.cheestree.vetly.domain.model.input.user.UserLoginInputModel
import com.cheestree.vetly.domain.model.input.user.UserRegisterInputModel
import com.cheestree.vetly.domain.path.Path.Users.CREATE
import com.cheestree.vetly.domain.path.Path.Users.GET
import com.cheestree.vetly.domain.path.Path.Users.LOGIN
import com.cheestree.vetly.domain.path.Path.Users.LOGOUT
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.UserProfile
import com.cheestree.vetly.services.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService
) {
    @GetMapping(GET)
    fun getUserProfile(
        @PathVariable userId: Long
    ): ResponseEntity<UserProfile> {
        return ResponseEntity.ok(userService.getUserById(userId))
    }

    @PostMapping(LOGIN)
    fun login(
        @RequestBody @Valid login: UserLoginInputModel,
        response: HttpServletResponse
    ): ResponseEntity<AuthenticatedUser> {
        return ResponseEntity.ok(userService.login(login.idToken))
    }

    @GetMapping(LOGOUT)
    @ProtectedRoute(MEMBER)
    fun logout(): String {
        //  No need, treated on interceptor
        return "logout"
    }

    @PostMapping(CREATE)
    fun register(
        @RequestBody @Valid register: UserRegisterInputModel
    ): ResponseEntity<UserProfile> {
        return ResponseEntity.ok(userService.register(register.uid, register.username, register.email))
    }
}