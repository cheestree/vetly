package com.cheestree.vetly.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import java.io.FileInputStream
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod", "dev")
class ProdFirebaseConfig {
    @PostConstruct
    fun init() {
        val path = System.getenv("FIREBASE_CREDENTIALS_PATH")
        val firebaseOptions = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(path)))
            .build()
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(firebaseOptions)
            println("Firebase initialized manually.")
        } else {
            println("Firebase already initialized.")
        }
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
