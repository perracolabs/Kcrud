/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.repositories.employment

import kcrud.server.data.tables.ContactTable
import kcrud.server.data.tables.EmployeeTable
import kcrud.server.data.tables.EmploymentTable
import kcrud.server.domain.entities.employment.Employment
import kcrud.server.domain.entities.employment.EmploymentRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class EmploymentRepository : IEmploymentRepository {
    override fun findById(employeeId: UUID, employmentId: UUID): Employment? {
        return transaction {
            (EmploymentTable innerJoin EmployeeTable leftJoin ContactTable)
                .selectAll().where {
                    (EmploymentTable.id eq employmentId) and
                            (EmploymentTable.employeeId eq employeeId)
                }.singleOrNull()?.let { resultRow ->
                    Employment.from(row = resultRow)
                }
        }
    }

    override fun findByEmployeeId(employeeId: UUID): List<Employment> {
        return transaction {
            (EmploymentTable innerJoin EmployeeTable leftJoin ContactTable)
                .selectAll().where {
                    (EmploymentTable.employeeId eq employeeId)
                }
                .map { resultRow ->
                    Employment.from(row = resultRow)
                }
        }
    }

    override fun create(employeeId: UUID, employmentRequest: EmploymentRequest): UUID {
        return transaction {
            EmploymentTable.insert { employmentRow ->
                employmentRequestToTable(
                    employeeId = employeeId,
                    employmentRequest = employmentRequest,
                    target = employmentRow
                )
            } get EmploymentTable.id
        }
    }

    override fun update(employeeId: UUID, employmentId: UUID, employmentRequest: EmploymentRequest): Int {
        return transaction {
            EmploymentTable.update(where = {
                (EmploymentTable.id eq employmentId) and (EmploymentTable.employeeId eq employeeId)
            }) { employmentRow ->
                employmentRequestToTable(
                    employeeId = employeeId,
                    employmentRequest = employmentRequest,
                    target = employmentRow
                )
            }
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
     * Populates an SQL [UpdateBuilder] with data from an [EmploymentRequest] instance.
     */
    private fun employmentRequestToTable(employeeId: UUID, employmentRequest: EmploymentRequest, target: UpdateBuilder<Int>) {
        target.apply {
            this[EmploymentTable.employeeId] = employeeId
            this[EmploymentTable.probationEndDate] = employmentRequest.probationEndDate
            this[EmploymentTable.workModality] = employmentRequest.workModality
            this[EmploymentTable.isActive] = employmentRequest.period.isActive
            this[EmploymentTable.startDate] = employmentRequest.period.startDate
            this[EmploymentTable.endDate] = employmentRequest.period.endDate
            this[EmploymentTable.comments] = employmentRequest.period.comments?.trim()
        }
    }
}
