package com.cheestree.vetly.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    @Profile("dev")
    fun devSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .headers { it.frameOptions { it.disable() } }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()

    @Bean
    @Profile("prod")
    fun prodSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .headers {
                it
                    .frameOptions { it.sameOrigin() }
                    .contentSecurityPolicy { it.policyDirectives("default-src 'self'") }
            }.authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
}
