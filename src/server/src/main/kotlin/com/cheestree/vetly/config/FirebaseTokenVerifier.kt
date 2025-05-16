package com.cheestree.vetly.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class DevFirebaseTokenVerifier : FirebaseTokenVerifier {
    override fun verify(token: String): FirebaseToken? =
        try {
            FirebaseAuth.getInstance().verifyIdToken(token)
        } catch (e: FirebaseAuthException) {
            null
        }
}

@Component
@Profile("prod")
class ProdFirebaseTokenVerifier : FirebaseTokenVerifier {
    override fun verify(token: String): FirebaseToken? =
        try {
            FirebaseAuth.getInstance().verifySessionCookie(token, true)
        } catch (e: FirebaseAuthException) {
            null
        }
}

@Component
@Profile("test")
class TestFirebaseTokenVerifier : FirebaseTokenVerifier {
    override fun verify(token: String): FirebaseToken? {
        println("Test profile: Skipping Firebase token verification")
        return null
    }
}

interface FirebaseTokenVerifier {
    fun verify(token: String): FirebaseToken?
}
