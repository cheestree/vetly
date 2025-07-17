package com.cheestree.vetly.config.file

import com.cheestree.vetly.config.AppConfig
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class FileUrlConfigInitializer(
    private val appConfig: AppConfig,
) {
    @PostConstruct
    fun init() {
        FileUrlConfig.initialize(appConfig.firebase.bucketName)
    }
}
