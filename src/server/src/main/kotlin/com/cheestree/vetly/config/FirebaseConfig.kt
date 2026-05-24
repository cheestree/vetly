package com.cheestree.vetly.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.FileInputStream

@Configuration
class FirebaseConfig(
    private val appConfig: AppConfig,
    private val env: Environment,
) {
    @PostConstruct
    fun init() {
        val activeProfiles = env.activeProfiles.toSet()
        if ("test" in activeProfiles) {
            println("Firebase initialization skipped for tests.")
            return
        }

        val envPath = System.getenv("FIREBASE_CREDENTIALS_PATH")
        require(!envPath.isNullOrBlank()) { "FIREBASE_CREDENTIALS_PATH must be set for non-test profiles" }

        val firebaseOptions =
            FileInputStream(envPath).use { credentialsInputStream ->
                FirebaseOptions
                    .builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsInputStream))
                    .setStorageBucket(appConfig.firebase.bucketName)
                    .build()
            }

        FirebaseApp.initializeApp(firebaseOptions)
        println("Firebase initialized successfully.")
    }
}
