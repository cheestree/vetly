package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.veterinarian.Veterinarian
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "clinic", schema = "vetly")
class Clinic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, updatable = false)
    val uuid: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    val nif: String,

    val name: String,
    val address: String,
    val long: Double,
    val lat: Double,
    val phone: String,
    val email: String,

    @Column(nullable = true)
    val imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User,

    @ManyToMany(mappedBy = "clinics")
    val veterinarians : MutableList<Veterinarian>,

    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true)
    val medicalSupplies: Set<MedicalSupplyClinic> = emptySet()
)