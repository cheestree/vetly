package com.cheestree.vetly.http.model.input.supply

import com.cheestree.vetly.domain.medicalsupply.supply.types.SupplyType
import org.springframework.data.domain.Sort

data class SupplyQueryInputModel(
    val name: String? = null,
    val type: SupplyType? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "name",
    val sortDirection: Sort.Direction = Sort.Direction.DESC,
)
