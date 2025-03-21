package com.cheestree.vetly.domain.medicalsupply

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import jakarta.persistence.*

@Entity
@Table(name = "supply", schema = "vetly")
class MedicalSupply (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    val description: String? = null,
    val imageUrl: String? = null,

    @OneToMany(mappedBy = "medicalSupply", cascade = [CascadeType.ALL], orphanRemoval = true)
    val clinics: Set<MedicalSupplyClinic> = emptySet()
)