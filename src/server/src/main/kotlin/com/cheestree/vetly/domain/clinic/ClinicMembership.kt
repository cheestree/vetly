package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.user.User
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
    var leftIn: OffsetDateTime? = null

) : BaseEntity()