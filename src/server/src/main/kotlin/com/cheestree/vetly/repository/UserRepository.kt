package com.cheestree.vetly.repository

import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.veterinarian.Veterinarian
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByUid(uid: String): Optional<User>


    //  List of all veterinarians
    @Query("SELECT v FROM Veterinarian v")
    fun findAllVeterinarians(): List<Veterinarian>

    //  Veterinarian by id
    @Query("SELECT v FROM Veterinarian v WHERE v.id = :id")
    fun findVeterinarianById(@Param("id") id: Long): Optional<Veterinarian>
}