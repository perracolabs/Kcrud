/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.data.models.shared.Period
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class Employment(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val id: UUID? = null,
    val period: Period,
    val probationEndDate: LocalDate? = null,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val employee: Employee? = null
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
                probationEndDate = row[EmploymentTable.probationEndDate],
                employee = Employee.fromTableRow(row)
            )
        }
    }
}
