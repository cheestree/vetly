package com.cheestree.vetly.repository.supply

import com.cheestree.vetly.domain.medicalsupply.medicalsupplyclinic.MedicalSupplyClinic
import com.cheestree.vetly.domain.medicalsupply.supply.MedicalSupply
import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import com.cheestree.vetly.repository.BaseSpecs

object SupplySpecs {
    fun byClinicId(clinicId: Long?) =
        BaseSpecs.equalObjectLong<MedicalSupplyClinic>(
            clinicId,
            "clinic",
            "id",
        )

    fun byMedicalSupplyClinicName(medicalSupply: String?) =
        BaseSpecs.likeObjectString<MedicalSupplyClinic>(
            medicalSupply,
            "medicalSupply",
            "name",
        )

    fun byMedicalSupplyClinicType(medicalSupplyType: SupplyType?) =
        BaseSpecs.equalObjectEnum<MedicalSupplyClinic, SupplyType>(
            medicalSupplyType,
            "medicalSupply",
            "type",
        )

    fun byMedicalSupplyName(medicalSupply: String?) =
        BaseSpecs.likeString<MedicalSupply>(
            medicalSupply,
            "name",
        )

    fun byMedicalSupplyType(medicalSupplyType: SupplyType?) =
        BaseSpecs.equalEnum<MedicalSupply, SupplyType>(
            medicalSupplyType,
            "type",
        )
}
