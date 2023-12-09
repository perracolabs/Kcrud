/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.employment

import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.data.models.employee.Employee
import com.kcrud.data.models.shared.Period
import com.kcrud.utils.SUUID
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents the model for an employment.
 *
 * @property id The employment's id.
 * @property period The employment's period.
 * @property probationEndDate The employment's probation end date.
 * @property employee The employment's employee.
 */
@Serializable
data class Employment(
    val id: SUUID,
    val period: Period,
    val probationEndDate: LocalDate? = null,
    val employee: Employee
) {
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
