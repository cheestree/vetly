package com.cheestree.vetly.repository

import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import java.time.ZoneOffset

object BaseSpecs {
    fun <T : Any> likeString(
        str: String?,
        value: String,
    ) = Specification<T> { root, _, cb ->
        str?.takeIf { it.isNotBlank() }?.let {
            cb.like(cb.lower(root.get(value)), "%${it.lowercase()}%")
        } ?: cb.conjunction()
    }

    fun <T : Any> likeObjectString(
        str: String?,
        objectName: String,
        value: String,
    ) = Specification<T> { root, _, cb ->
        str?.takeIf { it.isNotBlank() }?.let {
            val join = root.get<T>(objectName)
            cb.like(cb.lower(join.get(value)), "%${it.lowercase()}%")
        } ?: cb.conjunction()
    }

    fun <T : Any> equalDouble(
        id: Double?,
        value: String,
    ) = Specification<T> { root, _, cb ->
        id?.let {
            cb.equal(root.get<Double>(value), it)
        } ?: cb.conjunction()
    }

    fun <T : Any> equalLong(
        id: Long?,
        value: String,
    ) = Specification<T> { root, _, cb ->
        id?.let {
            cb.equal(root.get<Long>(value), it)
        } ?: cb.conjunction()
    }

    fun <T : Any> equalObjectLong(
        id: Long?,
        objectName: String,
        value: String,
    ) = Specification<T> { root, _, cb ->
        id?.let {
            val join = root.get<T>(objectName)
            cb.equal(join.get<Long>(value), it)
        } ?: cb.conjunction()
    }

    fun <T : Any, E : Enum<E>> equalEnum(
        enum: E?,
        value: String,
    ) = Specification<T> { root, _, cb ->
        enum?.let {
            cb.equal(root.get<E>(value), it)
        } ?: cb.conjunction()
    }

    fun <T : Any, E : Enum<E>> equalObjectEnum(
        enum: E?,
        objectName: String,
        value: String,
    ) = Specification<T> { root, _, cb ->
        enum?.let {
            val join = root.get<T>(objectName)
            cb.equal(join.get<E>(value), it)
        } ?: cb.conjunction()
    }

    fun <T : Any> betweenDates(
        from: LocalDate?,
        to: LocalDate?,
        value: String,
    ) = Specification<T> { root, _, cb ->
        val zoneOffset = ZoneOffset.UTC
        listOfNotNull(
            from?.atStartOfDay()?.atOffset(zoneOffset)?.let {
                cb.greaterThanOrEqualTo(root.get(value), it)
            },
            to?.plusDays(1)?.atStartOfDay()?.atOffset(zoneOffset)?.let {
                cb.lessThan(root.get(value), it)
            },
        ).takeIf { it.isNotEmpty() }?.let {
            cb.and(*it.toTypedArray())
        } ?: cb.conjunction()
    }

    fun <T : Any> combineAll(vararg specs: Specification<T>?): Specification<T> =
        specs.filterNotNull().reduce { acc, spec -> acc.and(spec) }
}
