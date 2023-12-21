/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employment

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.data.models.employment.Employment
import com.kcrud.data.models.employment.EmploymentParams
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class EmploymentRepository : IEmploymentRepository {
    override fun findById(employeeId: UUID, employmentId: UUID): Employment? {
        return transaction {
            (EmploymentTable innerJoin EmployeeTable leftJoin ContactTable)
                .select {
                    (EmploymentTable.id eq employmentId) and
                            (EmploymentTable.employeeId eq employeeId) and
                            (EmployeeTable.id eq employeeId) and
                            (ContactTable.employeeId eq employeeId)
                }.singleOrNull()?.let { resultRow ->
                    Employment.fromTableRow(row = resultRow)
                }
        }
    }

    override fun findByEmployeeId(employeeId: UUID): List<Employment> {
        return transaction {
            (EmploymentTable innerJoin EmployeeTable leftJoin ContactTable)
                .select { (EmploymentTable.employeeId eq employeeId) and (ContactTable.employeeId eq employeeId) }
                .map { resultRow ->
                    Employment.fromTableRow(row = resultRow)
                }
        }
    }

    override fun create(employeeId: UUID, employment: EmploymentParams): Employment {
        return transaction {
            val newEmploymentId = EmploymentTable.insert { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            } get EmploymentTable.id

            findById(employeeId = employeeId, employmentId = newEmploymentId)!!
        }
    }

    override fun update(employeeId: UUID, employmentId: UUID, employment: EmploymentParams): Employment? {
        return transaction {
            EmploymentTable.update(where = {
                EmploymentTable.id eq employmentId and (EmploymentTable.employeeId eq employeeId)
            }) { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            }

            findById(employeeId = employeeId, employmentId = employmentId)
        }
    }

    override fun delete(employmentId: UUID): Int {
        return transaction {
            EmploymentTable.deleteWhere { id eq employmentId }
        }
    }

    override fun deleteAll(employeeId: UUID): Int {
        return transaction {
            EmploymentTable.deleteWhere { EmploymentTable.employeeId eq employeeId }
        }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [EmploymentParams] model instance.
     */
    private fun employmentModelToTable(employeeId: UUID, employment: EmploymentParams, target: UpdateBuilder<Int>) {
        target.apply {
            this[EmploymentTable.employeeId] = employeeId
            this[EmploymentTable.probationEndDate] = employment.probationEndDate
            this[EmploymentTable.workModality] = employment.workModality
            this[EmploymentTable.isActive] = employment.period.isActive
            this[EmploymentTable.startDate] = employment.period.startDate
            this[EmploymentTable.endDate] = employment.period.endDate
            this[EmploymentTable.comments] = employment.period.comments?.trim()

            // PeriodColumns.fromPeriodModel(target = this, period = employment.period, columns = Employments.period)
        }
    }
}
