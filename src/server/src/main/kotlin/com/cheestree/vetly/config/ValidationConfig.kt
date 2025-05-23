package com.cheestree.vetly.config

import com.cheestree.vetly.config.EnvUtils.validatePathExists
import com.cheestree.vetly.config.EnvUtils.validatePort
import com.cheestree.vetly.config.EnvUtils.validateProfile
import com.cheestree.vetly.config.EnvUtils.validateRequired
import jakarta.annotation.PostConstruct
import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment

@Configuration
@Profile("prod")
class ValidationConfig(
    private val env: Environment,
) : WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    override fun customize(factory: ConfigurableWebServerFactory) {
        val port = env.getProperty("SPRING_PORT")?.toIntOrNull()

        if (port != null && port in 1024..65535) {
            factory.setPort(port)
            println("SPRING_PORT set to $port")
        } else {
            error("SPRING_PORT is invalid or not set. Falling back to default port")
        }
    }

    @PostConstruct
    fun validateEnv() {
        env.validateRequired("POSTGRES_DB")
        env.validateRequired("POSTGRES_HOST")
        env.validateRequired("POSTGRES_USER")
        env.validateRequired("POSTGRES_PASSWORD")

        env.validatePort("POSTGRES_PORT")
        env.validatePort("SPRING_PORT")

        env.validateProfile("SPRING_PROFILES_ACTIVE", listOf("prod", "dev", "test"))
        env.validatePathExists("FIREBASE_CREDENTIALS_PATH")

        println("Environment validation passed")
    }
}
