package com.cheestree.vetly.utils

import com.cheestree.vetly.config.AppConfig
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class Mapper(
    private val appConfig: AppConfig
) {
    fun buildPublicUrl(path: String): String {
        return "https://firebasestorage.googleapis.com/v0/b/${appConfig.firebase.bucketName}/o/${URLEncoder.encode(path, StandardCharsets.UTF_8)}?alt=media"
    }
}