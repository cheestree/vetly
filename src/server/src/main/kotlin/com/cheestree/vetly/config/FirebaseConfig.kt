package com.cheestree.vetly.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.FileInputStream
import java.io.FileNotFoundException

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
        val credentialsInputStream =
            try {
                if (!envPath.isNullOrBlank()) {
                    println("Trying to load Firebase credentials from environment path: $envPath")
                    FileInputStream(envPath)
                } else {
                    throw FileNotFoundException("Environment variable not set or empty.")
                }
            } catch (e: Exception) {
                println("Failed to load Firebase credentials from env path. Falling back to classpath. Reason: ${e.message}")
                javaClass.classLoader.getResourceAsStream("serviceAccount.json")
                    ?: throw IllegalStateException("Fallback credential not found in classpath.")
            }

        val firebaseOptions =
            FirebaseOptions
                .builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsInputStream))
                .setStorageBucket(appConfig.firebase.bucketName)
                .build()

        FirebaseApp.initializeApp(firebaseOptions)
        println("Firebase initialized successfully.")
    }
}