package com.cheestree.vetly.domain.clinic

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.user.User
import com.cheestree.vetly.domain.veterinarian.Veterinarian
import com.cheestree.vetly.http.model.output.clinic.ClinicInformation
import com.cheestree.vetly.http.model.output.clinic.ClinicPreview
import jakarta.persistence.*

@Entity
@Table(name = "clinic", schema = "vetly")
class Clinic(
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User? = null,

    @ManyToMany(mappedBy = "clinics")
    val veterinarians : Set<Veterinarian>,

    @OneToMany(mappedBy = "clinic", cascade = [CascadeType.ALL], orphanRemoval = true)
    val medicalSupplies: Set<MedicalSupplyClinic> = emptySet()
) {
    fun copy(
        nif: String = this.nif,
        name: String = this.name,
        address: String = this.address,
        long: Double = this.long,
        lat: Double = this.lat,
        phone: String = this.phone,
        email: String = this.email,
        imageUrl: String? = this.imageUrl,
        owner: User? = this.owner,
        veterinarians: Set<Veterinarian> = this.veterinarians,
        medicalSupplies: Set<MedicalSupplyClinic> = this.medicalSupplies
    ) = Clinic(id, nif, name, address, long, lat, phone, email, imageUrl, owner, veterinarians, medicalSupplies)

    fun asPublic() = ClinicInformation(
        name,
        address,
        long,
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