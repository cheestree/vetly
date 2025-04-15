package com.cheestree.vetly.service

import com.cheestree.vetly.domain.exception.VetException
import com.cheestree.vetly.domain.exception.VetException.ResourceNotFoundException
import com.cheestree.vetly.domain.user.AuthenticatedUser
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.user.roles.Role
import com.cheestree.vetly.domain.user.roles.RoleEntity
import com.cheestree.vetly.domain.user.userrole.UserRole
import com.cheestree.vetly.domain.user.userrole.UserRoleId
import com.cheestree.vetly.http.model.output.user.UserInformation
import com.cheestree.vetly.repository.UserRepository
import com.cheestree.vetly.repository.UserRoleRepository
import com.google.firebase.auth.FirebaseAuth
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userRoleRepository: UserRoleRepository,
    private val roleRepository: RoleRepository
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

        userRoleRepository.save(userRole)
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }
}
