package com.cheestree.vetly.domain.clinic.openinghour

import com.cheestree.vetly.domain.clinic.Clinic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalTime

@Entity
@Table(name = "clinic_opening_hours", schema = "vetly")
class OpeningHour(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    var weekday: Int, // 0 = Sunday, 6 = Saturday
    @Column(nullable = false)
    var opensAt: LocalTime,
    @Column(nullable = false)
    var closesAt: LocalTime,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    var clinic: Clinic,
)
