package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserById(id: Long): UserInformation {
        return userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User $id not found")
        }.asPublic()
    }

    fun getUserByUid(uid: String): UserInformation {
        return userRepository.findByUid(uid).orElseThrow {
            ResourceNotFoundException("User $uid not found")
        }.asPublic()
    }

    fun login(idToken: String): AuthenticatedUser {
        val decodedToken = authenticateFirebaseToken(idToken)

        val user = userRepository.findByEmail(decodedToken.email).orElseThrow{
            UnauthorizedAccessException("User not found")
        }

        return user.toAuthenticatedUser()
    }

    fun register(uid: String, username: String, email: String): UserInformation {
        val user = User(
            uid = uid,
            name = username,
            email = email,
            roles = listOf()
        )
        return userRepository.save(user).asPublic()
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }

    fun authenticateFirebaseToken(idToken: String): FirebaseToken {
        return FirebaseAuth.getInstance().verifyIdToken(idToken)
    }
}
