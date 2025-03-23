package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.enums.Role.MEMBER
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.UserProfile
import com.cheestree.vetly.http.model.input.user.UserLoginInputModel
import com.cheestree.vetly.http.model.input.user.UserRegisterInputModel
import com.cheestree.vetly.http.path.Path.Users.CREATE
import com.cheestree.vetly.http.path.Path.Users.GET
import com.cheestree.vetly.http.path.Path.Users.LOGIN
import com.cheestree.vetly.http.path.Path.Users.LOGOUT
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