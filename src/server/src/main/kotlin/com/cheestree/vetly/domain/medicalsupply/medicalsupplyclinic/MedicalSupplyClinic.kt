package com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "medical_supply_clinic", schema = "vetly")
open class MedicalSupplyClinic (
    @EmbeddedId
    val id: MedicalSupplyClinicId,

    @ManyToOne
    @MapsId("medicalSupply")
    val medicalSupply: MedicalSupply,

    @ManyToOne
    @MapsId("clinic")
    val clinic: Clinic,

    @Column(nullable = false)
    var price: BigDecimal,

    @Column(nullable = false)
    var quantity: Int
){
    fun updateWith(
        price: BigDecimal?,
        quantity: Int?
    ) {
        price?.let { this.price = it }
        quantity?.let { this.quantity = it }
    }

    fun asPublic(): MedicalSupplyClinicInformation {
        return MedicalSupplyClinicInformation(
            id = this.id.medicalSupply,
            name = this.medicalSupply.name,
            description = this.medicalSupply.asPublic(),
            quantity = this.quantity,
            price = this.price,
            type = this.medicalSupply::class.simpleName,
        )
    }
}