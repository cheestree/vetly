package com.cheestree.vetly.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Configuration
class FirebaseTokenVerifier {
    @Component
    @Profile("dev")
    class DevVerifier : Verifier {
        override fun verify(token: String): FirebaseToken? =
            try {
                FirebaseAuth.getInstance().verifyIdToken(token)
            } catch (e: FirebaseAuthException) {
                null
            }
    }

    @Component
    @Profile("prod")
    class ProdVerifier : Verifier {
        override fun verify(token: String): FirebaseToken? {
            return try {
                FirebaseAuth.getInstance().verifySessionCookie(token, true)
            } catch (e: FirebaseAuthException) {
                return try {
                    FirebaseAuth.getInstance().verifyIdToken(token)
                } catch (e: FirebaseAuthException) {
                    null
                }
            }
        }
    }

    @Component
    @Profile("test")
    class TestVerifier : Verifier {
        override fun verify(token: String): FirebaseToken? {
            println("Test profile: Skipping Firebase token verification")
            return null
        }
    }

    interface Verifier {
        fun verify(token: String): FirebaseToken?
    }
}