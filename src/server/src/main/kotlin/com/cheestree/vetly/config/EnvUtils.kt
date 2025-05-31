package com.cheestree.vetly.config

import java.io.File
import org.springframework.core.env.Environment

object EnvUtils {
    fun Environment.validateRequired(name: String) {
        val value = this.getProperty(name)
        require(!value.isNullOrBlank()) { "Missing required environment variable: $name" }
    }

    fun Environment.validateOrigins(name: String) {
        val value = this.getProperty(name)
        require(!value.isNullOrBlank()) { "Missing CORS allowed origins: $name" }
        val allowedOrigins = System.getenv("cors.allowed-origins")?.split(",")?.map { it.trim() }?.toTypedArray()
        require(!allowedOrigins.isNullOrEmpty()) { "CORS allowed origins cannot be empty: $name" }
    }

    fun Environment.validatePort(name: String) {
        val value = this.getProperty(name)
        requireNotNull(value) { "Missing port: $name" }
        val port = value.toIntOrNull()
        require(port in 1024..65535) {
            "Invalid value for $name: '$value'. Must be a number between 1024 and 65535"
        }
    }

    fun Environment.validateProfile(
        name: String,
        validProfiles: List<String>,
    ) {
        val value = this.getProperty(name)
        require(!value.isNullOrBlank()) {
            "Missing $name. Must be one of: ${validProfiles.joinToString()}"
        }
        require(value in validProfiles) {
            "Invalid $name: '$value'. Must be one of: ${validProfiles.joinToString()}"
        }
    }

    fun Environment.validatePathExists(name: String) {
        val path = this.getProperty(name)
        if (path.isNullOrBlank()) {
            println("$name not set — skipping file existence check")
            return
        }
        val file = File(path)
        require(file.exists() && file.isFile) {
            "$name points to invalid file: '$path'"
        }
    }
}
