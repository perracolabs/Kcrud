/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employment

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.data.models.employment.EmploymentRequest
import com.kcrud.data.models.employment.EmploymentResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class EmploymentRepository : IEmploymentRepository {
    override fun findById(employmentId: UUID): EmploymentResponse? {
        return transaction {
            (EmploymentTable innerJoin EmployeeTable innerJoin ContactTable)
                .select {
                    (EmploymentTable.id eq employmentId) and
                            (EmployeeTable.id eq EmploymentTable.employeeId) and
                            (EmployeeTable.contactId eq ContactTable.id)
                }.singleOrNull()?.let { resultRow ->
                    EmploymentResponse.fromTableRow(row = resultRow)
                }
        }
    }

    override fun findByEmployeeId(employeeId: UUID): List<EmploymentResponse> {
        return transaction {
            (EmploymentTable innerJoin EmployeeTable innerJoin ContactTable)
                .select { (EmploymentTable.employeeId eq employeeId) and (EmployeeTable.contactId eq ContactTable.id) }
                .map { resultRow ->
                    EmploymentResponse.fromTableRow(row = resultRow)
                }
        }
    }

    override fun create(employeeId: UUID, employment: EmploymentRequest): EmploymentResponse {
        return transaction {
            val newEmploymentId = EmploymentTable.insert { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            } get EmploymentTable.id

            findById(newEmploymentId)!!
        }
    }

    override fun update(employeeId: UUID, employmentId: UUID, employment: EmploymentRequest): EmploymentResponse? {
        return transaction {
            EmploymentTable.update(where = { EmploymentTable.id eq employmentId }) { employmentRow ->
                employmentModelToTable(employeeId = employeeId, employment = employment, target = employmentRow)
            }

            findById(employmentId)
        }
    }

    override fun delete(employmentId: UUID): Int {
        return transaction {
            EmploymentTable.deleteWhere { id eq employmentId }
        }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [EmploymentRequest] model instance.
     */
    private fun employmentModelToTable(employeeId: UUID, employment: EmploymentRequest, target: UpdateBuilder<Int>) {
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
