package com.cheestree.vetly.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("app")
@Component
data class AppConfig(
    val paging: Paging = Paging(),
    val format: Format = Format(),
    val cors: Cors = Cors(),
) {
    data class Paging(
        val defaultPageSize: Int = 10,
        val maxPageSize: Int = 100,
    )

    data class Format(
        val dateFormat: String = "yyyy-MM-dd'T'HH:mm:ssXXX",
        val timeZone: String = "UTC",
    )

    data class Cors(
        var allowedOrigins: List<String> = emptyList(),
    )
}
