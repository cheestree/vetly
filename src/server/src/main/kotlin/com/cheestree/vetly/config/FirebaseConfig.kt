package com.cheestree.vetly.config

import com.google.firebase.FirebaseApp
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("prod", "dev")
class ProdFirebaseConfig {
    @PostConstruct
    fun init() {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp()
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
