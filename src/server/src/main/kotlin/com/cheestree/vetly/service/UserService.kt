package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.exception.VetException.UnauthorizedAccessException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleChangeRequest
import com.cheestree.vetly.http.model.output.user.RoleRequestInformation
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.repository.RoleChangeRequestRepository
import com.cheestree.vetly.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleChangeRequestRepository: RoleChangeRequestRepository
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

    fun requestRole(
        userId: Long,
        requestedRole: String,
        justification: String?,
        fileUrl: String?
    ): RoleRequestInformation {
        val user = userRepository.findById(userId).orElseThrow {
            ResourceNotFoundException("User $userId not found")
        }

        val role = Role.fromDbValue(requestedRole)
            ?: throw IllegalArgumentException("Invalid role requested")

        if (user.roles.any { it.role.role == role }) {
            throw UnauthorizedAccessException("User already has the requested role")
        }

        val roleRequest = RoleChangeRequest(
            user = user,
            requestedRole = requestedRole,
            justification = justification,
            fileUrl = fileUrl
        )

        return roleChangeRequestRepository.save(roleRequest).toInformation()
    }
}
