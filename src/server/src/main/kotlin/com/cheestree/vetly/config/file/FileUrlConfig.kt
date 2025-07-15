package com.cheestree.vetly.config.file

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object FileUrlConfig {
    private lateinit var bucketName: String

    fun initialize(bucketName: String) {
        FileUrlConfig.bucketName = bucketName
    }

    fun buildPublicUrl(path: String): String {
        return "https://firebasestorage.googleapis.com/v0/b/$bucketName/o/${URLEncoder.encode(path, StandardCharsets.UTF_8)}?alt=media"
    }
}