package com.cheestree.vetly

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("app")
data class AppConfig(
    val defaultPageSize: Int = 10,
    val maxPageSize: Int = 100,
)