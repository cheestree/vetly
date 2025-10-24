package com.cheestree.vetly.domain.request

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.file.File
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.request.RequestInformation
import com.cheestree.vetly.http.model.output.request.RequestPreview
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "requests", schema = "vetly")
class Request(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val action: RequestAction,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val target: RequestTarget,
    var justification: String?,
    @OneToMany(mappedBy = "request", cascade = [CascadeType.ALL])
    val files: List<File> = listOf(),
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
            status = status,
            justification = justification,
            files = files.map { it.asInformation() },
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
