package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "clinics", schema = "vetly")
class Clinic(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    var nif: String,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var address: String,
    @Column(nullable = false)
    var longitude: Double,
    @Column(nullable = false)
    var latitude: Double,
    @Column(nullable = false)
    var phone: String,
    @Column(nullable = true)
    var email: String,
    @Column(nullable = true)
    var imageUrl: String? = null,
    @Column(nullable = false)
    var isActive: Boolean = true,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    var owner: User? = null,
    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true)
    val clinicMemberships: MutableSet<ClinicMembership> = mutableSetOf(),
    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true)
    val medicalSupplies: MutableSet<MedicalSupplyClinic> = mutableSetOf(),
) : BaseEntity() {
    fun updateWith(
        nif: String?,
        name: String?,
        address: String?,
        lng: Double?,
        lat: Double?,
        phone: String?,
        email: String?,
        imageUrl: String?,
        owner: User?,
    ) {
        nif?.let { this.nif = it }
        name?.let { this.name = it }
        address?.let { this.address = it }
        lng?.let { this.longitude = it }
        lat?.let { this.latitude = it }
        phone?.let { this.phone = it }
        email?.let { this.email = it }
        imageUrl?.let { this.imageUrl = it }
        owner?.let { this.owner = it }
    }

    fun asPublic() =
        ClinicInformation(
            id,
            name,
            address,
            longitude,
            latitude,
            phone,
            email,
            imageUrl,
            owner?.asPreview(),
        )

    fun asPreview() =
        ClinicPreview(
            id,
            name,
            imageUrl,
        )
}
