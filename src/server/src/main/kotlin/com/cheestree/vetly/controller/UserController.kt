package com.cheestree.vetly.controller

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.api.UserApi
import com.cheestree.vetly.http.model.input.user.UserLoginInputModel
import com.cheestree.vetly.http.model.input.user.UserUpdateInputModel
import com.cheestree.vetly.http.model.output.user.UserAuthenticated
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class UserController(
    private val userService: UserService,
) : UserApi {
    override fun login(
        loggedUser: UserLoginInputModel,
        response: HttpServletResponse,
    ): ResponseEntity<UserAuthenticated> = ResponseEntity.ok(userService.login(loggedUser.token, response))

    override fun logout(request: HttpServletRequest): ResponseEntity<*> = ResponseEntity.ok(userService.logout(request))

    @AuthenticatedRoute
    override fun getUserProfile(id: UUID): ResponseEntity<UserInformation> = ResponseEntity.ok(userService.getUserByPublicId(id))

    @AuthenticatedRoute
    override fun getMyProfile(user: AuthenticatedUser): ResponseEntity<UserInformation> {
        //  AuthenticatedUser ALWAYS has a UID given by the database on creation
        return ResponseEntity.ok(userService.getSelfByUid(user.uid))
    }

    @AuthenticatedRoute
    override fun updateMyProfile(
        user: AuthenticatedUser,
        updatedUser: UserUpdateInputModel,
    ): ResponseEntity<UserInformation> = ResponseEntity.ok(userService.updateUserProfile(user, updatedUser))
}
