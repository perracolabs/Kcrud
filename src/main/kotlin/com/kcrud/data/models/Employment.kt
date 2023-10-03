/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models

import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.data.models.shared.Period
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Employment(
    val id: Int? = null,
    val period: Period,
    val probationEndDate: LocalDate? = null
) {
    init {
        probationEndDate?.let { date ->
            require(date >= period.startDate) { "Employment probation end date cannot be earlier than period start date." }
        }
    }

    companion object {
        fun fromTableRow(row: ResultRow): Employment {
            return Employment(
                id = row[EmploymentTable.id],
                period = Period.fromTableRow(row = row, table = EmploymentTable),
                probationEndDate = row[EmploymentTable.probationEndDate]
            )
        }
    }
}
