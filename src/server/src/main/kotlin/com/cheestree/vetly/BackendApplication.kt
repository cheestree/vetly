package com.cheestree.vetly

import com.cheestree.vetly.config.AppConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableConfigurationProperties(AppConfig::class)
@SpringBootApplication
@EnableCaching
open class BackendApplication

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
