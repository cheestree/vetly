package com.cheestree.vetly.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Profile("dev")
class DevSecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .headers { it.frameOptions { it.disable() } }
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
}

@Configuration
@Profile("prod")
class ProdSecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .headers { headers ->
                headers
                    .frameOptions { it.sameOrigin() }
                    .contentSecurityPolicy { it.policyDirectives("default-src 'self'") }
            }.authorizeHttpRequests { auth -> auth.anyRequest().permitAll() }
            .build()
}
