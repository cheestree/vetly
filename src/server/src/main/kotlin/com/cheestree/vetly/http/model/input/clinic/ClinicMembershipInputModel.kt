package com.cheestree.vetly.http.model.input.clinic

import com.cheestree.vetly.http.model.input.request.RequestExtraData

data class ClinicMembershipInputModel(
    val clinicId: Long,
) : RequestExtraData
