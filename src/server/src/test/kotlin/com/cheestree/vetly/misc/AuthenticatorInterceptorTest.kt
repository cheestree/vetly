package com.cheestree.vetly.misc

import com.cheestree.vetly.config.FirebaseTokenVerifier
import com.cheestree.vetly.domain.annotation.AuthenticatedRoute
import com.cheestree.vetly.domain.annotation.ProtectedRoute
import com.cheestree.vetly.domain.exception.VetException.ForbiddenException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.domain.user.userrole.types.VeterinarianRole
import com.cheestree.vetly.http.AuthenticatorInterceptor
import com.cheestree.vetly.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.Method
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthenticatorInterceptorTest {
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    private lateinit var method: Method
    private lateinit var handler: HandlerMethod
    private lateinit var userService: UserService
    private lateinit var firebaseTokenVerifier: FirebaseTokenVerifier.Verifier
    private lateinit var interceptor: AuthenticatorInterceptor

    companion object {
        const val TEST_USER_ID = 1L
        const val TEST_ROLE_ID = 1L
        const val USERNAME = "DrVet"
        const val EMAIL = "vet@example.com"
    }

    @BeforeEach
    fun setup() {
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()

        method = mockk<Method>()
        handler = mockk<HandlerMethod>()
        every { handler.method } returns method

        every { method.name } returns "testMethod"

        val authenticatedRouteAnnotation = mockk<AuthenticatedRoute>(relaxed = true)

        every { method.getAnnotation(AuthenticatedRoute::class.java) } returns authenticatedRouteAnnotation
        every { method.annotations } returns arrayOf(authenticatedRouteAnnotation)

        userService = mockk()
        firebaseTokenVerifier = mockk()
        interceptor = spyk(AuthenticatorInterceptor(userService, firebaseTokenVerifier))

        val authenticatedUser = mockk<AuthenticatedUser>()
        every { interceptor.extractUserInfo(request) } returns authenticatedUser
    }

    @Test
    fun `should allow access to protected route for authorized role`() {
        val user =
            User(
                id = TEST_USER_ID,
                username = USERNAME,
                email = EMAIL,
                roles =
                    mutableSetOf(
                        UserRole(
                            id = UserRoleId(userId = TEST_USER_ID, roleId = TEST_ROLE_ID),
                            user = mockk(relaxed = true),
                            role = VeterinarianRole(id = TEST_ROLE_ID, name = Role.VETERINARIAN.name),
                        ),
                    ),
                uid = "553",
            )

        val authenticatedUser =
            AuthenticatedUser(
                id = TEST_USER_ID,
                roles = setOf(Role.VETERINARIAN),
                email = user.email,
                name = user.username,
                publicId = user.publicId,
                uid = "1141",
            )

        every { method.getAnnotation(AuthenticatedRoute::class.java) } returns null
        every { method.getAnnotation(ProtectedRoute::class.java) } returns ProtectedRoute(Role.VETERINARIAN)

        every { interceptor.extractUserInfo(request) } returns authenticatedUser

        val result = interceptor.preHandle(request, response, handler)

        assertTrue(result)
        assertEquals(authenticatedUser, request.getAttribute("authenticatedUser"))
    }

    @Test
    fun `should throw ForbiddenException when user lacks required role`() {
        val authenticatedUser = mockk<AuthenticatedUser>()
        every { authenticatedUser.roles } returns setOf(Role.VETERINARIAN)

        every { method.getAnnotation(AuthenticatedRoute::class.java) } returns null
        every { method.getAnnotation(ProtectedRoute::class.java) } returns ProtectedRoute(Role.ADMIN)

        every { interceptor.extractUserInfo(request) } returns authenticatedUser

        assertThrows<ForbiddenException> {
            interceptor.preHandle(request, response, handler)
        }
    }

    @Test
    fun `should allow access to authenticated-only route`() {
        val handler = mockk<HandlerMethod>()
        every { handler.method } returns method
        every { method.getAnnotation(ProtectedRoute::class.java) } returns null

        val authenticatedUser = mockk<AuthenticatedUser>()
        every { interceptor.extractUserInfo(request) } returns authenticatedUser

        val result = interceptor.preHandle(request, response, handler)

        assertTrue(result, "Expected result to be true")
        assertEquals(authenticatedUser, request.getAttribute("authenticatedUser"))
    }
}
