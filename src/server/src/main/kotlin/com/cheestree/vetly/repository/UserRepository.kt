package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, Long> {
    // Find a user by ID and check if they have the Veterinarian role
    @Query(
        """
        SELECT u FROM User u
        JOIN u.roles ur
        WHERE u.id = :id AND ur.role.role = 'VETERINARIAN'
    """,
    )
    fun findVeterinarianById(
        @Param("id") id: Long,
    ): Optional<User>

    fun findByPublicId(publicId: UUID): Optional<User>

    fun findByUid(uid: String): Optional<User>

    fun getUserByUid(uid: String): Optional<User>

    fun findByEmail(email: String): Optional<User>
}
