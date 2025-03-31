package com.cheestree.vetly.domain.user.roles

import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.user.RoleRequestInformation
import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "role_requests", schema = "vetly")
class RoleChangeRequest(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    val requestedRole: String,
    val status: Status = Status.PENDING,
    val justification: String?,
    val fileUrl: String?,
    val submittedAt: OffsetDateTime = OffsetDateTime.now()
){
    fun asPublic() = RoleRequestInformation(
        id = id,
        requestedRole = requestedRole,
        status = status,
        justification = justification,
        fileUrl = fileUrl,
        submittedAt = submittedAt
    )
}