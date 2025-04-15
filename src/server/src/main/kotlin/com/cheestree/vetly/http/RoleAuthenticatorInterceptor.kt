package com.cheestree.vetly.http

import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.roles.Role
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
            if (handler.method.getAnnotation(AuthenticatedRoute::class.java) != null) {
                val authenticatedUser = extractUserInfo(request) ?: run {
                    throw UnauthorizedAccessException("User not authenticated")
                }
                request.setAttribute("authenticatedUser", authenticatedUser)
                return true
            }

            val roleRoute = handler.method.getAnnotation(ProtectedRoute::class.java) ?: return true

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

    private fun checkMethodAccess(requiredRole: Role, userRoles: Set<Role>): Boolean {
        if (userRoles.isEmpty()) return false
        return roleHierarchy[userRoles.firstOrNull()]?.contains(requiredRole) ?: false
    }

    private val roleHierarchy = mapOf(
        Role.ADMIN to setOf(Role.ADMIN, Role.VETERINARIAN),
        Role.VETERINARIAN to setOf(Role.VETERINARIAN)
    )

    fun extractUserInfo(request: HttpServletRequest): AuthenticatedUser? {
        val idToken = getAuthTokenFromHeader(request) ?: return null
        val decodedToken = verifyFirebaseToken(idToken) ?: return null

        val email = decodedToken.email ?: return null

        val user = userService.getUserByEmail(email) ?: return null

        return user.toAuthenticatedUser()
    }

    private fun getAuthTokenFromHeader(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization") ?: return null
        return if (authorizationHeader.startsWith("Bearer ")) {
            authorizationHeader.substring(7)  //    Extract the token after "Bearer "
        } else {
            null
        }
    }

    private fun verifyFirebaseToken(idToken: String): FirebaseToken? {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(idToken)
        } catch (e: FirebaseAuthException) {
            null
        }
    }
}