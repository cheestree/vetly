package com.cheestree.vetly.domain.guide

import com.cheestree.vetly.domain.veterinarian.Veterinarian
import jakarta.persistence.*

@Entity
@Table(name = "guide", schema = "vetly")
class Guide(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,
    val description: String,

    @Column(nullable = true)
    val imageUrl: String? = null,

    val text: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", referencedColumnName = "id")
    val veterinarian: Veterinarian
)