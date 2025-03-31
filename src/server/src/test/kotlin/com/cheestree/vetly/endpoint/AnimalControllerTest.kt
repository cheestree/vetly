package com.cheestree.vetly.endpoint

import com.cheestree.vetly.controller.AnimalController
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.FirebaseAuthenticationFilter
import com.cheestree.vetly.http.RoleAuthenticatorInterceptor
import com.cheestree.vetly.http.path.Path
import com.cheestree.vetly.service.AnimalService
import com.cheestree.vetly.service.UserService
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AnimalController::class)
class AnimalControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockK
    lateinit var firebaseAuthenticationFilter: FirebaseAuthenticationFilter

    @MockK
    lateinit var roleAuthenticatorInterceptor: RoleAuthenticatorInterceptor

    @MockK
    lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    @MockitoBean
    lateinit var animalService: AnimalService

    @MockitoBean
    lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        val dummyUser = AuthenticatedUser(id = 1, name = "Dummy", email = "test@example.com", roles = listOf(Role.ADMIN))

        // Mock FirebaseAuth and FirebaseToken
        every { firebaseAuthenticationFilter.doFilterInternal(any(), any(), any()) } returns Unit

        // Mock other dependencies
        every { roleAuthenticatorInterceptor.preHandle(any(), any(), any()) } returns true
        every { authenticatedUserArgumentResolver.supportsParameter(any()) } returns true
        every { authenticatedUserArgumentResolver.resolveArgument(any(), any(), any(), any()) } returns dummyUser
    }
    @Test
    fun `should return 400 if animalId is invalid`() {
        mockMvc.perform(get(Path.Animals.GET, "invalid"))
            .andDo {
                print(it.response.contentAsString)
            }
            .andExpect(status().isBadRequest)
    }
}