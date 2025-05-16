package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException.ResourceAlreadyExistsException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.ResourceType
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.http.AuthenticatorInterceptor
import com.cheestree.vetly.http.model.output.user.UserAuthenticated
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.repository.RoleRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.repository.UserRoleRepository
import com.cheestree.vetly.service.Utils.Companion.createResource
import com.cheestree.vetly.service.Utils.Companion.retrieveResource
import com.cheestree.vetly.service.Utils.Companion.updateResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.google.firebase.auth.SessionCookieOptions
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.time.Duration
import java.util.Date
import java.util.UUID
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val roleRepository: RoleRepository,
) {
    fun login(idToken: String, response: HttpServletResponse): UserAuthenticated {
        val firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
        val user = userRepository.findByUid(firebaseToken.uid).orElseGet { createUser(firebaseToken) }

        val sessionCookie = FirebaseAuth.getInstance().createSessionCookie(
            idToken,
            SessionCookieOptions.builder()
                .setExpiresIn(60 * 60 * 24 * 7 * 1000L)
                .build()
        )

        val cookie = ResponseCookie.from(AuthenticatorInterceptor.AUTH_COOKIE, sessionCookie)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofDays(7))
            .sameSite("None")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        return UserAuthenticated(sessionCookie, user.asPublic())
    }

    fun logout(request: HttpServletRequest) {
        val cookie = Cookie(AuthenticatorInterceptor.AUTH_COOKIE, "").apply {
            path = "/"
            maxAge = 0
            isHttpOnly = true   
            secure = true
        }

        request.setAttribute("setCookie", cookie)
    }

    fun getSelfByUid(uid: String): UserInformation {
        return retrieveResource(ResourceType.USER, uid) {
            userRepository.getUserByUid(uid).orElseThrow {
                ResourceNotFoundException(ResourceType.USER, uid)
            }.asPublic()
        }
    }

    fun getUserByPublicId(publicId: UUID): UserInformation {
        return retrieveResource(ResourceType.USER, publicId) {
            userRepository.findByPublicId(publicId).orElseThrow {
                ResourceNotFoundException(ResourceType.USER, publicId)
            }.asPublic()
        }
    }

    fun getUserByUid(uid: String): User? {
        return retrieveResource(ResourceType.USER, uid) {
            userRepository.findByUid(uid).orElse(null)
        }
    }

    fun createUser(firebaseUser: FirebaseToken): User {
        return createResource(ResourceType.USER) {
            userRepository.findByUid(firebaseUser.uid).orElseGet {
                val newUser =
                    User(
                        uid = firebaseUser.uid,
                        username = firebaseUser.name,
                        email = firebaseUser.email,
                        imageUrl = firebaseUser.picture,
                    )
                userRepository.save(newUser)
            }
        }
    }

    fun updateUserProfile(
        userId: Long,
        username: String? = null,
        imageUrl: String? = null,
        phone: Int? = null,
        birthDate: Date? = null,
    ): UserInformation {
        return updateResource(ResourceType.USER, userId) {
            val user =
                userRepository.findById(userId).orElseThrow {
                    ResourceNotFoundException(ResourceType.USER, userId)
                }

            user.updateWith(username, imageUrl, phone, birthDate)

            userRepository.save(user).asPublic()
        }
    }

    fun updateUserRole(
        userId: Long,
        role: Role,
    ) {
        return updateResource(ResourceType.USER, userId) {
            val roleEntity =
                roleRepository.findByRole(role).orElseThrow {
                    ResourceNotFoundException(ResourceType.ROLE, role)
                }

            val user =
                userRepository.findById(userId).orElseThrow {
                    ResourceNotFoundException(ResourceType.USER, userId)
                }

            if (userRoleRepository.existsById(UserRoleId(user.id, roleEntity.id))) {
                throw ResourceAlreadyExistsException(ResourceType.ROLE, "userId-roleId", UserRoleId(user.id, roleEntity.id))
            }

            val userRole =
                UserRole(
                    id = UserRoleId(userId = user.id, roleId = roleEntity.id),
                    user = user,
                    role = roleEntity,
                )

            user.addRole(userRole)

            userRepository.save(user)
            userRoleRepository.save(userRole)
        }
    }
}
