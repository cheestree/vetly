package com.cheestree.vetly.http.model.output.animal

import com.cheestree.vetly.converter.CustomOffsetDateTimeDeserializer
import com.cheestree.vetly.converter.CustomOffsetDateTimeSerializer
import com.cheestree.vetly.http.model.output.user.UserPreview
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.OffsetDateTime

data class AnimalInformation(
    val id: Long,
    val name: String,
    val microchip: String?,
    val species: String?,

    @JsonSerialize(using = CustomOffsetDateTimeSerializer::class)
    @JsonDeserialize(using = CustomOffsetDateTimeDeserializer::class)
    val birthDate: OffsetDateTime?,

    val imageUrl: String?,
    val age: Int?,
    val owner: UserPreview?
)