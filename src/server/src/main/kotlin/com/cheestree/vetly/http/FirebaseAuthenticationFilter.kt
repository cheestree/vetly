package com.cheestree.vetly.http

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.service.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.HandlerMapping

class FirebaseAuthenticationFilter(
    private val userService: UserService
) : OncePerRequestFilter() {

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val handler = (request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE) as? HandlerMethod)

        val isProtected = handler?.hasMethodAnnotation(ProtectedRoute::class.java)
            ?: handler?.beanType?.isAnnotationPresent(ProtectedRoute::class.java)
            ?: false

        if(!isProtected) {
            return filterChain.doFilter(request, response)
        }

        val token = extractToken(request)

        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            try {
                val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
                val uid = decodedToken.uid
                val user = userService.getUserByUid(uid)

                val auth = UsernamePasswordAuthenticationToken(user, null)
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: FirebaseAuthException) {
                logger.warn("Firebase authentication failed: ${e.message}")
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        return request.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }?.removePrefix("Bearer ")
    }
}