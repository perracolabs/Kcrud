/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employment

import com.kcrud.data.database.entities.Contacts
import com.kcrud.data.database.entities.Employees
import com.kcrud.data.database.entities.Employments
import com.kcrud.data.models.Employment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class EmploymentRepository : IEmploymentRepository {
    override fun findById(employmentId: Int): Employment? {
        return transaction {
            Employments.select { Employments.id eq employmentId }.singleOrNull()?.let { resultRow ->
                Employment.fromTableRow(row = resultRow)
            }
        }
    }

    override fun findByEmployeeId(employeeId: Int): List<Employment> {
        return transaction {
            (Employments innerJoin Employees innerJoin Contacts)
                .select { Employments.employeeId eq employeeId and (Employees.contactId eq Contacts.id) }
                .map { resultRow ->
                    Employment.fromTableRow(row = resultRow)
                }
        }
    }

    override fun create(employeeId: Int, employment: Employment): Employment {
        return transaction {
            val newEmploymentId = Employments.insert { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            } get Employments.id

            findById(newEmploymentId)!!
        }
    }

    override fun update(employeeId: Int, employmentId: Int, employment: Employment): Employment? {
        return transaction {
            Employments.update(where = { Employments.id eq employmentId }) { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            }

            findById(employmentId)
        }
    }

    override fun delete(employmentId: Int): Int {
        return transaction {
            Employments.deleteWhere { id eq employmentId }
        }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [Employment] model instance.
     */
    private fun employmentModelToTable(employeeId: Int, employment: Employment, target: UpdateBuilder<Int>) {
        target.apply {
            this[Employments.employeeId] = employeeId
            this[Employments.probationEndDate] = employment.probationEndDate
            this[Employments.isActive] = employment.period.isActive
            this[Employments.startDate] = employment.period.startDate
            this[Employments.endDate] = employment.period.endDate
            this[Employments.comments] = employment.period.comments?.trim()

           // PeriodColumns.fromPeriodModel(target = this, period = employment.period, columns = Employments.period)
        }
    }
}
