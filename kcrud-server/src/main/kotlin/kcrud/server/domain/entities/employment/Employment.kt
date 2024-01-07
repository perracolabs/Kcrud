/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.entities.employment

import kcrud.base.data.serializers.SUUID
import kcrud.server.data.tables.EmploymentTable
import kcrud.server.domain.entities.employee.Employee
import kcrud.server.domain.entities.employment.types.WorkModality
import kcrud.server.domain.entities.shared.Period
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents the entity for an employment.
 *
 * @property id The employment's id.
 * @property period The employment's period details.
 * @property probationEndDate Optional employment's probation end date.
 * @property workModality The employment's work modality.
 * @property employee The employment's employee.
 */
@Serializable
data class Employment(
    val id: SUUID,
    val period: Period,
    val probationEndDate: LocalDate? = null,
    val workModality: WorkModality,
    val employee: Employee
) {
    companion object {
        fun toEntity(row: ResultRow): Employment {
            return Employment(
                id = row[EmploymentTable.id],
                period = Period.toEntity(row = row, table = EmploymentTable),
                probationEndDate = row[EmploymentTable.probationEndDate],
                workModality = row[EmploymentTable.workModality],
                employee = Employee.toEntity(row = row)
            )
        }
    }
}
