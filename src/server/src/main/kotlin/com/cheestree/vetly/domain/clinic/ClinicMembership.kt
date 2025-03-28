package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "clinic_membership", schema = "vetly")
class ClinicMembership(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: ClinicMembershipId,

    @ManyToOne
    @MapsId("veterinarian")
    val veterinarian: User,

    @ManyToOne
    @MapsId("clinic")
    val clinic: Clinic
)