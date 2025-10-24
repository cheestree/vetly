package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.clinic.ClinicMembershipPreview
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "clinic_memberships", schema = "vetly")
class ClinicMembership(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: ClinicMembershipId,
    @ManyToOne
    @MapsId("veterinarian")
    val veterinarian: User,
    @ManyToOne
    @MapsId("clinic")
    val clinic: Clinic,
    @Column(nullable = true)
    var leftIn: OffsetDateTime? = null,
) : BaseEntity() {
    fun asPreview() =
        ClinicMembershipPreview(
            user = veterinarian.asLink(),
            clinic = clinic.asLink(),
            joinedAt = createdAt.toLocalDate(),
            leftIn = leftIn?.toLocalDate(),
        )

    override fun toString(): String = "${this.clinic.name} - ${this.veterinarian.username}"
}
