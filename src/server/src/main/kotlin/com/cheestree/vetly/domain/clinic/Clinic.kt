package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.clinic.openinghour.OpeningHour
import com.cheestree.vetly.domain.clinic.service.ServiceType
import com.cheestree.vetly.domain.file.File
import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicLink
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import com.cheestree.vetly.http.model.output.clinic.OpeningHourInformation
import jakarta.persistence.*

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
    @OneToOne(mappedBy = "clinic", cascade = [CascadeType.ALL])
    var image: File? = null,
    @Column(nullable = false)
    var isActive: Boolean = true,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    var owner: User? = null,
    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true)
    val clinicMemberships: MutableSet<ClinicMembership> = mutableSetOf(),
    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val medicalSupplies: MutableSet<MedicalSupplyClinic> = mutableSetOf(),
    @ElementCollection(targetClass = ServiceType::class, fetch = FetchType.EAGER)
    @CollectionTable(
        name = "clinic_services",
        schema = "vetly",
        joinColumns = [JoinColumn(name = "clinic_id")],
    )
    @Column(name = "service", nullable = false)
    @Enumerated(EnumType.STRING)
    val services: Set<ServiceType> = mutableSetOf(),
    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true)
    val openingHours: MutableSet<OpeningHour> = mutableSetOf(),
) : BaseEntity() {
    fun updateWith(
        nif: String?,
        name: String?,
        address: String?,
        lng: Double?,
        lat: Double?,
        phone: String?,
        email: String?,
        image: File?,
        owner: User?,
    ) {
        nif?.let { this.nif = it }
        name?.let { this.name = it }
        address?.let { this.address = it }
        lng?.let { this.longitude = it }
        lat?.let { this.latitude = it }
        phone?.let { this.phone = it }
        email?.let { this.email = it }
        image?.let { this.image = it }
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
            openingHours.map { OpeningHourInformation(it.weekday, it.opensAt.toString(), it.closesAt.toString()) },
            image?.asInformation(),
            services,
            owner?.asPreview(),
        )

    fun asLink() =
        ClinicLink(
            id = this.id,
            name = this.name,
            image = this.image?.asPreview(),
        )

    fun asPreview() =
        ClinicPreview(
            id = this.id,
            name = this.name,
            address = this.address,
            phone = this.phone,
            image = this.image?.asPreview(),
            services = this.services.toList(),
            openingHours =
                this.openingHours.map {
                    OpeningHourInformation(
                        weekday = it.weekday,
                        opensAt = it.opensAt.toString(),
                        closesAt = it.closesAt.toString(),
                    )
                },
        )

    override fun toString(): String = "${this.id}: ${this.name} | ${this.clinicMemberships}"
}
