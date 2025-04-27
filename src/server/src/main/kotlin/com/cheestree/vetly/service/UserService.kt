package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.repository.RoleRepository
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.repository.UserRoleRepository
import com.google.firebase.auth.FirebaseAuth
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val roleRepository: RoleRepository
) {
    fun getUserByUid(uid: String): UserInformation {
        return userRepository.findByUid(uid).orElseThrow {
            ResourceNotFoundException("User $uid not found")
        }.asPublic()
    }

    fun getUserByPublicId(publicId: UUID): UserInformation {
        return userRepository.findByPublicId(publicId).orElseThrow {
            ResourceNotFoundException("User $publicId not found")
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
                roles = mutableSetOf()
            )
            userRepository.save(newUser)
        }

        return user.toAuthenticatedUser()
    }

    fun updateUserRole(userId: Long, role: Role) {
        val roleEntity = roleRepository.findByRole(role).orElseThrow {
            VetException.BadRequestException("Role $role not found")
        }

        val user = userRepository.findById(userId).orElseThrow {
            ResourceNotFoundException("User $userId not found")
        }

        if (userRoleRepository.existsById(UserRoleId(user.id, roleEntity.id)))
            throw IllegalStateException("User already has this role assigned")

        val userRole = UserRole(
            id = UserRoleId(userId = user.id, roleId = roleEntity.id),
            user = user,
            role = roleEntity
        )

        user.addRole(userRole)

        userRepository.save(user)
        userRoleRepository.save(userRole)
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }
}
