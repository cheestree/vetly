package com.cheestree.vetly.controller

import com.cheestree.vetly.api.UserApi
import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.http.model.input.user.UserLoginInputModel
import com.cheestree.vetly.http.model.input.user.UserUpdateInputModel
import com.cheestree.vetly.http.model.output.user.UserAuthenticated
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserController(
    private val userService: UserService,
) : UserApi {
    override fun login(
        input: UserLoginInputModel,
        response: HttpServletResponse,
    ): ResponseEntity<UserAuthenticated> = ResponseEntity.ok(userService.login(input.token, response))

    override fun logout(request: HttpServletRequest): ResponseEntity<*> = ResponseEntity.ok(userService.logout(request))

    @AuthenticatedRoute
    override fun getUserProfile(userId: UUID): ResponseEntity<UserInformation> = ResponseEntity.ok(userService.getUserByPublicId(userId))

    @AuthenticatedRoute
    override fun getMyProfile(authenticatedUser: AuthenticatedUser): ResponseEntity<UserInformation> {
        //  AuthenticatedUser ALWAYS has a UID given by the database on creation
        return ResponseEntity.ok(userService.getSelfByUid(authenticatedUser.uid!!))
    }

    @AuthenticatedRoute
    override fun updateMyProfile(
        authenticatedUser: AuthenticatedUser,
        input: UserUpdateInputModel,
    ): ResponseEntity<UserInformation> =
        ResponseEntity.ok(
            userService.updateUserProfile(
                userId = authenticatedUser.id,
                username = input.username,
                imageUrl = input.imageUrl,
                phone = input.phone,
                birthDate = input.birthDate,
            ),
        )
}
