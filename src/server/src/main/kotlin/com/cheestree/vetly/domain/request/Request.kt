package com.cheestree.vetly.domain.request

import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "request", schema = "vetly")
class Request(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    val action: RequestAction,

    @Enumerated(EnumType.STRING)
    val target: RequestTarget,

    val justification: String?,

    //  If files become more complex (needs more metadata), create separate entity
    @ElementCollection
    val files: List<String>,

    @Enumerated(EnumType.STRING)
    var requestStatus: RequestStatus = RequestStatus.PENDING,

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    val extraData: Map<String, Any?>? = null,
    val submittedAt: OffsetDateTime
){
    fun copy(
        requestStatus: RequestStatus = this.requestStatus,
    ): Request = Request(
        id = this.id,
        user = this.user,
        action = this.action,
        target = this.target,
        justification = this.justification,
        files = this.files,
        requestStatus = requestStatus,
        extraData = this.extraData,
        submittedAt = this.submittedAt
    )

    fun asPublic() = RequestInformation(
        id = id,
        user = user.asPublic(),
        target = target.name,
        action = action.name,
        requestStatus = requestStatus,
        justification = justification,
        files = files,
        extraData = extraData,
        submittedAt = submittedAt
    )

    fun asPreview() = RequestPreview(
        id = id,
        user = user.asPreview(),
        target = target.name,
        action = action.name,
        status = requestStatus.name,
        justification = justification,
        submittedAt = submittedAt
    )
}