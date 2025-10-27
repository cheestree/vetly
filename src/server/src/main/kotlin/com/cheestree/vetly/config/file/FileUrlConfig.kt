package com.cheestree.vetly.config.file

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object FileUrlConfig {
    private lateinit var bucketName: String

    fun initialize(bucketName: String) {
        FileUrlConfig.bucketName = bucketName
    }

    fun buildPublicUrl(path: String): String {
        val normalized = path.removePrefix("/")
        val encoded = URLEncoder.encode(normalized, StandardCharsets.UTF_8.name())
        return "https://firebasestorage.googleapis.com/v0/b/$bucketName/o/$encoded?alt=media"
    }
}
