package com.cheestree.vetly.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import java.io.FileInputStream
import java.io.FileNotFoundException
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod", "dev")
class ProdFirebaseConfig {
    @PostConstruct
    fun init() {
        if (FirebaseApp.getApps().isNotEmpty()) {
            println("Firebase already initialized.")
            return
        }

        val envPath = System.getenv("FIREBASE_CREDENTIALS_PATH")
        val credentialsInputStream = try {
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

        val firebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(credentialsInputStream))
            .build()

        FirebaseApp.initializeApp(firebaseOptions)
        println("Firebase initialized successfully.")
    }
}

@Configuration
@Profile("test")
class TestFirebaseConfig {
    @PostConstruct
    fun init() {
        println("Firebase initialization skipped for tests.")
    }
}
