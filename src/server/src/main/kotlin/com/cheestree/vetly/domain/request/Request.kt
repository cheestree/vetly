package com.cheestree.vetly.domain.request

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "requests", schema = "vetly")
class Request(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Enumerated(EnumType.STRING)
    val action: RequestAction,
    @Enumerated(EnumType.STRING)
    val target: RequestTarget,
    var justification: String?,
    //  If files become more complex (needs more metadata), create separate entity
    @ElementCollection
    val files: List<String>,
    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.PENDING,
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    val extraData: Map<String, Any?>? = null,
) : BaseEntity() {
    fun updateWith(requestStatus: RequestStatus? = null) {
        requestStatus?.let { this.status = it }
    }

    fun asPublic() =
        RequestInformation(
            id = id,
            user = user.asPublic(),
            target = target.name,
            action = action.name,
            requestStatus = status,
            justification = justification,
            files = files,
            extraData = extraData,
            createdAt = createdAt,
        )

    fun asPreview() =
        RequestPreview(
            id = id,
            user = user.asPreview(),
            target = target.name,
            action = action.name,
            status = status.name,
            justification = justification,
            createdAt = createdAt,
        )
}
