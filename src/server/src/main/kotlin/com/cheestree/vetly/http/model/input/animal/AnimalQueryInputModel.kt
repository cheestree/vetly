package com.cheestree.vetly.http.model.input.animal

import com.cheestree.vetly.domain.animal.sex.Sex
import com.cheestree.vetly.domain.annotation.Microchip
import jakarta.validation.constraints.Email
import org.springframework.data.domain.Sort
import java.time.LocalDate

data class AnimalQueryInputModel(
    @field:Email
    val userEmail: String? = null,
    val name: String? = null,
    @field:Microchip
    val microchip: String? = null,
    val sex: Sex? = null,
    val sterilized: Boolean? = null,
    val species: String? = null,
    val birthDate: LocalDate? = null,
    val owned: Boolean? = null,
    val self: Boolean? = null,
    val active: Boolean? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "name",
    val sortDirection: Sort.Direction = Sort.Direction.DESC,
)
