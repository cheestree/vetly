package com.cheestree.vetly.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("app")
data class AppConfig(
    val defaultPageSize: Int = 10,
    val maxPageSize: Int = 100,
    val dateFormat: String = "yyyy-MM-dd'T'HH:mm:ssXXX",
)
