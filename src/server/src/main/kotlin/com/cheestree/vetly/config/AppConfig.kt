package com.cheestree.vetly.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("app")
data class AppConfig(
    val paging: Paging = Paging(),
    val format: Format = Format()
) {
    data class Paging(
        val defaultPageSize: Int = 10,
        val maxPageSize: Int = 100
    )

    data class Format(
        val dateFormat: String = "yyyy-MM-dd'T'HH:mm:ssXXX",
        val timeZone: String = "UTC"
    )
}