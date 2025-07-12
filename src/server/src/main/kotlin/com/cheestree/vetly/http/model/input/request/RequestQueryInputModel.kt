package com.cheestree.vetly.http.model.input.request

import com.cheestree.vetly.domain.request.type.RequestAction
import com.cheestree.vetly.domain.request.type.RequestStatus
import com.cheestree.vetly.domain.request.type.RequestTarget
import org.springframework.data.domain.Sort
import java.time.LocalDate

data class RequestQueryInputModel(
    val userId: Long? = null,
    val userName: String? = null,
    val action: RequestAction? = null,
    val target: RequestTarget? = null,
    val status: RequestStatus? = null,
    val submittedBefore: LocalDate? = null,
    val submittedAfter: LocalDate? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String = "userId",
    val sortDirection: Sort.Direction = Sort.Direction.DESC
)