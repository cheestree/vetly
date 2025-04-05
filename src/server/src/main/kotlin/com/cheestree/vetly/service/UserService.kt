package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

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
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
        val uid = decodedToken.uid

        val user = userRepository.findByUid(uid).getOrElse {
            val newUser = User(
                uid = uid,
                email = decodedToken.email,
                username = decodedToken.name,
                imageUrl = decodedToken.picture,
                roles = setOf()
            )
            userRepository.save(newUser)
        }

        return user.toAuthenticatedUser()
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }
}
