package com.cheestree.vetly.http

import com.cheestree.vetly.config.FirebaseTokenVerifier
import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.service.UserService
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthenticatorInterceptor(
    private val userService: UserService,
    private val firebaseTokenVerifier: FirebaseTokenVerifier.Verifier,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (handler is HandlerMethod) {
            val method = handler.method
            val isAuthenticated = method.getAnnotation(AuthenticatedRoute::class.java) != null
            val protectedRoute = method.getAnnotation(ProtectedRoute::class.java)

            if (isAuthenticated || protectedRoute != null) {
                val authenticatedUser =
                    extractUserInfo(request)
                        ?: throw UnauthorizedAccessException("Token malformed")
                request.setAttribute("authenticatedUser", authenticatedUser)

                if (protectedRoute != null && !checkMethodAccess(protectedRoute.role, authenticatedUser.roles)) {
                    throw ForbiddenException("User does not have permission to access this route")
                }
            }
        }
        return true
    }

    fun extractUserInfo(request: HttpServletRequest): AuthenticatedUser? {
        val idToken = getAuthToken(request) ?: return null
        val decodedToken = verifyFirebaseToken(idToken) ?: return null

        val user = userService.getUserByUid(decodedToken.uid) ?: return null

        return user.toAuthenticatedUser()
    }

    private fun getAuthToken(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7)
        }

        val cookies = request.cookies ?: return null
        val authCookie = cookies.find { it.name == AUTH_COOKIE }
        return authCookie?.value
    }

    private fun verifyFirebaseToken(token: String): FirebaseToken? =
        try {
            firebaseTokenVerifier.verify(token)
        } catch (e: FirebaseAuthException) {
            null
        }

    private fun checkMethodAccess(
        requiredRole: Role,
        userRoles: Set<Role>,
    ): Boolean {
        if (userRoles.isEmpty()) return false
        return roleHierarchy[userRoles.firstOrNull()]?.contains(requiredRole) ?: false
    }

    private val roleHierarchy =
        mapOf(
            Role.ADMIN to setOf(Role.ADMIN, Role.VETERINARIAN),
            Role.VETERINARIAN to setOf(Role.VETERINARIAN),
        )

    companion object {
        const val AUTH_COOKIE = "auth_cookie"
    }
}
