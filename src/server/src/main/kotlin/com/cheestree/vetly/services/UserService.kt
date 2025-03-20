package com.cheestree.vetly.services

import com.cheestree.vetly.domain.enums.Role
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.UserProfile
import com.cheestree.vetly.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserById(id: Long): UserProfile {
        return userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User $id not found")
        }.toUserProfile()
    }

    fun getUserByUid(uid: String): UserProfile {
        return userRepository.findByUid(uid).orElseThrow {
            ResourceNotFoundException("User $uid not found")
        }.toUserProfile()
    }

    fun login(idToken: String): AuthenticatedUser {
        val decodedToken = authenticateFirebaseToken(idToken)

        val user = userRepository.findByEmail(decodedToken.email).orElseThrow{
            UnauthorizedAccessException("User not found")
        }

        return user.toAuthenticatedUser()
    }

    fun register(uid: String, username: String, email: String): UserProfile {
        val user = User(
            uid = uid,
            name = username,
            email = email,
            role = Role.MEMBER
        )
        return userRepository.save(user).toUserProfile()
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }

    fun authenticateFirebaseToken(idToken: String): FirebaseToken {
        return FirebaseAuth.getInstance().verifyIdToken(idToken)
    }
}
