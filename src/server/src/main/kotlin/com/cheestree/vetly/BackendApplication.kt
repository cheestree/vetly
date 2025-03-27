package com.cheestree.vetly

import com.cheestree.vetly.argument_resolver.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.filter.FirebaseAuthenticationFilter
import com.cheestree.vetly.interceptor.RoleAuthenticatorInterceptor
import com.cheestree.vetly.service.UserService
import com.google.firebase.FirebaseApp
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class BackendApplication

@Configuration
class WebConfig: WebMvcConfigurer {
	@Autowired
	private lateinit var protectedRouteInterceptor: RoleAuthenticatorInterceptor

	@Autowired
	private lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("*")
	}

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(protectedRouteInterceptor).addPathPatterns("/api/**")
	}

	override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
		resolvers.add(authenticatedUserArgumentResolver)
	}
}

@Configuration
class SecurityConfig(
	private val userService: UserService
) {
	@Bean
	fun firebaseAuthenticationFilter(): FirebaseAuthenticationFilter {
		return FirebaseAuthenticationFilter(userService)
	}

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		return http
			.csrf { it.disable() }
			.addFilterBefore(firebaseAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
			.authorizeHttpRequests { it.anyRequest().permitAll() }
			.build()
	}
}

fun main(args: Array<String>) {
	if (FirebaseApp.getApps().isEmpty()) {
		FirebaseApp.initializeApp()
		println("Firebase initialized successfully.")
	} else {
		println("Firebase already initialized.")
	}
	runApplication<BackendApplication>(*args)
}