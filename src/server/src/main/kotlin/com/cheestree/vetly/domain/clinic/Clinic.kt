package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.veterinarian.Veterinarian
import jakarta.persistence.*

@Entity
@Table(name = "clinic", schema = "vetly")
data class Clinic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

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

    @ManyToMany(mappedBy = "clinics")
    val veterinarians : MutableList<Veterinarian>
)