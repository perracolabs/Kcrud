/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.domain.entities.shared

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

/**
 * Represents a time period. Utilized via composition in other data classes
 * to enable sharing of common period-related properties,
 * as Kotlin data classes do not support inheritance.
 */
@Serializable
data class Period(
    val isActive: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val comments: String? = null
) {
    init {
        endDate?.let {
            require(startDate <= it) { "End Date cannot be earlier than Start Date." }
        }
    }

    companion object {
        fun toEntity(row: ResultRow, table: Table): Period {
            val isActiveCol = table.columns.single { it.name == "is_active" }
            val startDateCol = table.columns.single { it.name == "start_date" }
            val endDateCol = table.columns.single { it.name == "end_date" }
            val commentsCol = table.columns.single { it.name == "comments" }

            return Period(
                isActive = row[isActiveCol] as Boolean,
                startDate = row[startDateCol] as LocalDate,
                endDate = row[endDateCol] as LocalDate?,
                comments = row[commentsCol] as String?
            )
        }
    }
}
