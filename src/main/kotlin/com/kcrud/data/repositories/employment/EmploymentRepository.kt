/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employment

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.data.models.Employment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class EmploymentRepository : IEmploymentRepository {
    override fun findById(employmentId: Int): Employment? {
        return transaction {
            EmploymentTable.select { EmploymentTable.id eq employmentId }.singleOrNull()?.let { resultRow ->
                Employment.fromTableRow(row = resultRow)
            }
        }
    }

    override fun findByEmployeeId(employeeId: Int): List<Employment> {
        return transaction {
            (EmploymentTable innerJoin EmployeeTable innerJoin ContactTable)
                .select { EmploymentTable.employeeId eq employeeId and (EmployeeTable.contactId eq ContactTable.id) }
                .map { resultRow ->
                    Employment.fromTableRow(row = resultRow)
                }
        }
    }

    override fun create(employeeId: Int, employment: Employment): Employment {
        return transaction {
            val newEmploymentId = EmploymentTable.insert { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            } get EmploymentTable.id

            findById(newEmploymentId)!!
        }
    }

    override fun update(employeeId: Int, employmentId: Int, employment: Employment): Employment? {
        return transaction {
            EmploymentTable.update(where = { EmploymentTable.id eq employmentId }) { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            }

            findById(employmentId)
        }
    }

    override fun delete(employmentId: Int): Int {
        return transaction {
            EmploymentTable.deleteWhere { id eq employmentId }
        }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [Employment] model instance.
     */
    private fun employmentModelToTable(employeeId: Int, employment: Employment, target: UpdateBuilder<Int>) {
        target.apply {
            this[EmploymentTable.employeeId] = employeeId
            this[EmploymentTable.probationEndDate] = employment.probationEndDate
            this[EmploymentTable.isActive] = employment.period.isActive
            this[EmploymentTable.startDate] = employment.period.startDate
            this[EmploymentTable.endDate] = employment.period.endDate
            this[EmploymentTable.comments] = employment.period.comments?.trim()

           // PeriodColumns.fromPeriodModel(target = this, period = employment.period, columns = Employments.period)
        }
    }
}