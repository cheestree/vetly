package com.cheestree.vetly.interceptor

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.enums.Role
import com.cheestree.vetly.domain.exception.VetException.InsufficientPermissionException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.service.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RoleAuthenticatorInterceptor(
    private val userService: UserService
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val roleRoute = handler.method.getAnnotation(ProtectedRoute::class.java) ?: return true

            println("RoleAuthenticatorInterceptor: roleRoute: $roleRoute")

            val authenticatedUser = extractUserInfo(request) ?: run {
                throw UnauthorizedAccessException("User not authenticated")
            }

            if (!checkMethodAccess(roleRoute.role, authenticatedUser.roles)) {
                throw InsufficientPermissionException("User does not have permission to access this route")
            }

            request.setAttribute("authenticatedUser", authenticatedUser)
            return true
        }
        return true
    }

    private fun checkMethodAccess(requiredRole: Role, userRoles: List<Role>): Boolean {
        return roleHierarchy[userRoles.firstOrNull()]?.contains(requiredRole) ?: false
    }

    private val roleHierarchy = mapOf(
        Role.ADMIN to setOf(Role.ADMIN, Role.VETERINARIAN, Role.MEMBER),
        Role.VETERINARIAN to setOf(Role.VETERINARIAN, Role.MEMBER),
        Role.MEMBER to setOf(Role.MEMBER)
    )

    private fun extractUserInfo(request: HttpServletRequest): AuthenticatedUser? {
        val idToken = getCookie(request, "access_token") ?: return null
        val decodedToken = verifyFirebaseToken(idToken) ?: return null

        val user = userService.getUserByEmail(decodedToken.email ?: "") ?: return null

        return AuthenticatedUser(
            id = user.id,
            email = user.email,
            uid = user.uid,
            name = user.name,
            roles = user.roles
        )
    }

    private fun getCookie(request: HttpServletRequest, name: String): String? {
        return request.cookies?.firstOrNull { it.name == name }?.value
    }

    private fun verifyFirebaseToken(idToken: String): FirebaseToken? {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(idToken)
        } catch (e: FirebaseAuthException) {
            null
        }
    }
}