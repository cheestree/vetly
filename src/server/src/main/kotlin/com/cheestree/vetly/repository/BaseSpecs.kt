package com.cheestree.vetly.repository

import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.time.ZoneOffset

object BaseSpecs {
    fun <T> likeString(str: String?, value: String) = Specification<T> { root, _, cb ->
        str?.takeIf { it.isNotBlank() }?.let {
            cb.like(cb.lower(root.get(value)), "%${it.lowercase()}%")
        } ?: cb.conjunction()
    }

    fun <T> likeObjectString(str: String?, objectName: String, value: String) = Specification<T> { root, _, cb ->
        str?.takeIf { it.isNotBlank() }?.let {
            val join = root.get<T>(objectName)
            cb.like(cb.lower(join.get(value)), "%${it.lowercase()}%")
        } ?: cb.conjunction()
    }

    fun <T> equalDouble(id: Double?, value: String) = Specification<T> { root, _, cb ->
        id?.let {
            cb.equal(root.get<Double>(value), it)
        } ?: cb.conjunction()
    }

    fun <T> equalLong(id: Long?, value: String) = Specification<T> { root, _, cb ->
        id?.let {
            cb.equal(root.get<Long>(value), it)
        } ?: cb.conjunction()
    }

    fun <T> equalObjectLong(id: Long?, objectName: String, value: String) = Specification<T> { root, _, cb ->
        id?.let {
            val join = root.get<T>(objectName)
            cb.equal(join.get<Long>(value), it)
        } ?: cb.conjunction()
    }

    fun <T> betweenDates(from: LocalDate?, to: LocalDate?, value: String) = Specification<T> { root, _, cb ->
        val zoneOffset = ZoneOffset.UTC
        listOfNotNull(
            from?.atStartOfDay()?.atOffset(zoneOffset)?.let {
                cb.greaterThanOrEqualTo(root.get(value), it)
            },
            to?.plusDays(1)?.atStartOfDay()?.atOffset(zoneOffset)?.let {
                cb.lessThan(root.get(value), it)
            }
        ).takeIf { it.isNotEmpty() }?.let {
            cb.and(*it.toTypedArray())
        } ?: cb.conjunction()
    }
}