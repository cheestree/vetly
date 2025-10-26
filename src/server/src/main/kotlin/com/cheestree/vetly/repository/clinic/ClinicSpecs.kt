package com.cheestree.vetly.repository.clinic

import com.cheestree.vetly.domain.clinic.Clinic
import com.cheestree.vetly.repository.BaseSpecs

object ClinicSpecs {
    fun nameContains(name: String?) = BaseSpecs.likeString<Clinic>(name, "name")

    fun latitudeEquals(latitude: Double?) = BaseSpecs.equalDouble<Clinic>(latitude, "latitude")

    fun longitudeEquals(longitude: Double?) = BaseSpecs.equalDouble<Clinic>(longitude, "longitude")
}