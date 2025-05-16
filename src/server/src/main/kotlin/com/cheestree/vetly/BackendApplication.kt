package com.cheestree.vetly

import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.AuthenticatorInterceptor
import com.google.firebase.FirebaseApp
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class BackendApplication

@Configuration
class WebConfig : WebMvcConfigurer {
    @Autowired
    private lateinit var protectedRouteInterceptor: AuthenticatorInterceptor

    @Autowired
    private lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8081")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(protectedRouteInterceptor).addPathPatterns("/**")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }
}

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            // 	REMOVE THIS IN PROD, USED FOR H2
            .headers { it.frameOptions { it.disable() } }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
    }
}

@Configuration
class FirebaseConfig {
    @PostConstruct
    fun init() {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp()
            println("Firebase initialized manually.")
        } else {
            println("Firebase already initialized.")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
