package com.cheestree.vetly.http

import com.cheestree.vetly.domain.exception.VetException.BadRequestException
import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestTarget
import com.cheestree.vetly.http.model.input.clinic.ClinicCreateInputModel
import com.cheestree.vetly.http.model.input.clinic.ClinicMembershipInputModel
import com.cheestree.vetly.http.model.input.request.RequestExtraData
import com.cheestree.vetly.http.model.input.user.UserRoleUpdateInputModel
import kotlin.reflect.KClass

object RequestExtraDataTypeRegistry {
    private val mapping: Map<Pair<RequestTarget, RequestAction>, KClass<out RequestExtraData>> = mapOf(
        RequestTarget.CLINIC to RequestAction.CREATE to ClinicCreateInputModel::class,
        RequestTarget.ROLE to RequestAction.UPDATE to UserRoleUpdateInputModel::class,
        RequestTarget.CLINIC_MEMBERSHIP to RequestAction.CREATE to ClinicMembershipInputModel::class,
        RequestTarget.CLINIC_MEMBERSHIP to RequestAction.DELETE to ClinicMembershipInputModel::class,
    )

    fun expectedTypeFor(target: RequestTarget, action: RequestAction): KClass<out RequestExtraData> =
        mapping[target to action]
            ?: throw BadRequestException("Unsupported request target/action combination: $target to $action")
}
