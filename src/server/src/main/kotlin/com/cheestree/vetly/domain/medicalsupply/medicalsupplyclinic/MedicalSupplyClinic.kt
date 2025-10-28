package com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic

import com.cheestree.vetly.domain.BaseEntity
import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicInformation
import com.cheestree.vetly.http.model.output.supply.MedicalSupplyClinicPreview
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "medical_supplies_clinics", schema = "vetly")
class MedicalSupplyClinic(
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
    var quantity: Int,
) : BaseEntity() {
    fun updateWith(
        price: BigDecimal?,
        quantity: Int?,
    ) {
        price?.let { this.price = it }
        quantity?.let { this.quantity = it }
    }

    fun asPreview(): MedicalSupplyClinicPreview =
        MedicalSupplyClinicPreview(
            id = this.id.medicalSupply,
            name = this.medicalSupply.name,
            type = this.medicalSupply.type,
        )

    fun asPublic(): MedicalSupplyClinicInformation =
        MedicalSupplyClinicInformation(
            id = this.id.medicalSupply,
            name = this.medicalSupply.name,
            description = this.medicalSupply.asPublic(),
            quantity = this.quantity,
            price = this.price,
            type = this.medicalSupply::class.simpleName,
        )
}
