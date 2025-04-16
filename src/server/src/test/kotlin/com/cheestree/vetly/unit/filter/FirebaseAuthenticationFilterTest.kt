package com.cheestree.vetly.unit.filter

import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.FirebaseAuthenticationFilter
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.service.UserService
import com.google.common.base.Verify.verify
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.method.HandlerMethod
import org.springframework.web.reactive.HandlerMapping
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class FirebaseAuthenticationFilterTest {
    @MockK
    lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        SecurityContextHolder.clearContext()
    }

    @AfterEach
    fun teardown() {
        SecurityContextHolder.clearContext()
    }

    @BeforeEach
    fun init() {
        userService = mockk()
    }

    @Test
    fun `should authenticate user on protected route`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        val handlerMethod = mockk<HandlerMethod>()
        every { handlerMethod.hasMethodAnnotation(ProtectedRoute::class.java) } returns true
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, handlerMethod)

        val token = "validToken"
        request.addHeader("Authorization", "Bearer $token")

        val firebaseToken = mockk<FirebaseToken>()
        every { firebaseToken.uid } returns "firebase-uid"
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance().verifyIdToken(token) } returns firebaseToken

        val user = mockk<UserInformation>()
        every { userService.getUserByUid("firebase-uid") } returns user

        val filter = FirebaseAuthenticationFilter(userService)

        filter.doFilterInternal(request, response, filterChain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
        assertEquals(user, auth.principal)
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should skip authentication for unprotected route`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val filterChain = mockk<FilterChain>(relaxed = true)

        val handlerMethod = mockk<HandlerMethod>()
        every { handlerMethod.hasMethodAnnotation(ProtectedRoute::class.java) } returns false
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, handlerMethod)

        val filter = FirebaseAuthenticationFilter(userService)

        filter.doFilterInternal(request, response, filterChain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)
        verify { filterChain.doFilter(request, response) }
    }
}