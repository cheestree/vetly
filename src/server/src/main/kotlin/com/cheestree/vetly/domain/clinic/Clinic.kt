package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import jakarta.persistence.*

@Entity
@Table(name = "clinic", schema = "vetly")
open class Clinic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    var nif: String,

    var name: String,
    var address: String,
    var lng: Double,
    var lat: Double,
    var phone: String,
    var email: String,

    @Column(nullable = true)
    var imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    var owner: User? = null,

    @OneToMany(mappedBy = "clinic")
    val clinicMemberships: MutableSet<ClinicMembership> = mutableSetOf(),

    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true)
    val medicalSupplies: MutableSet<MedicalSupplyClinic> = mutableSetOf()
) {
    fun updateWith(
        nif: String?,
        name: String?,
        address: String?,
        lng: Double?,
        lat: Double?,
        phone: String?,
        email: String?,
        imageUrl: String?,
        owner: User?
    ) {
        nif?.let { this.nif = it }
        name?.let { this.name = it }
        address?.let { this.address = it }
        lng?.let { this.lng = it }
        lat?.let { this.lat = it }
        phone?.let { this.phone = it }
        email?.let { this.email = it }
        imageUrl?.let { this.imageUrl = it }
        owner?.let { this.owner = it }
    }

    fun asPublic() = ClinicInformation(
        id,
        name,
        address,
        lng,
        lat,
        phone,
        email,
        imageUrl,
        owner?.asPreview(),
    )

    fun asPreview() = ClinicPreview(
        id,
        name,
        imageUrl
    )
}