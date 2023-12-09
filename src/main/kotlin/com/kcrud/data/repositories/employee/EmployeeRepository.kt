/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.models.employee.EmployeeInput
import com.kcrud.data.models.employee.Employee
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class EmployeeRepository : IEmployeeRepository {

    override fun findById(employeeId: UUID): Employee? {
        return transaction {
            val query = EmployeeTable.join(
                otherTable = ContactTable,
                joinType = JoinType.INNER,
                onColumn = EmployeeTable.contactId,
                otherColumn = ContactTable.id
            ).select { EmployeeTable.id eq employeeId }

            query.map { resultRow ->
                Employee.fromTableRow(row = resultRow)
            }.singleOrNull()
        }
    }

    override fun findAll(): List<Employee> {
        return transaction {
            val query = EmployeeTable.join(
                otherTable = ContactTable,
                joinType = JoinType.INNER,
                onColumn = EmployeeTable.contactId,
                otherColumn = ContactTable.id
            ).selectAll()

            query.map { resultRow ->
                Employee.fromTableRow(row = resultRow)
            }
        }
    }

    override fun create(employee: EmployeeInput): Employee {
        return transaction {
            val newContactId = ContactTable.insert { contactRow ->
                contactRow[email] = employee.contact.email.trim()
                contactRow[phone] = employee.contact.phone.trim()
            } get ContactTable.id

            val newEmployeeId = EmployeeTable.insert { employeeRow ->
                employeeModelToTable(employee = employee, target = employeeRow, contactId=newContactId)
                employeeRow[contactId] = newContactId
            } get EmployeeTable.id

            findById(newEmployeeId)!!
        }
    }

    override fun update(employeeId: UUID, employee: EmployeeInput): Employee? {
        return transaction {
            // First, find the existing employee to get the contact id.
            val targetEmployee = findById(employeeId) ?: return@transaction null
            val dbContactId = targetEmployee.contact.id

            // Update the Contacts table.
            ContactTable.update(where = { ContactTable.id eq dbContactId }) { contactRow ->
                contactRow[email] = employee.contact.email.trim()
                contactRow[phone] = employee.contact.phone.trim()
            }

            // Update the Employees table.
            EmployeeTable.update(where = { EmployeeTable.id eq employeeId }) { employeeRow ->
                employeeModelToTable(employee = employee, target = employeeRow, contactId = dbContactId)
            }

            findById(employeeId)
        }
    }

    override fun delete(employeeId: UUID): Int {
        return transaction {
            EmployeeTable.deleteWhere { id eq employeeId }
        }
    }

    override fun deleteAll(): Int {
        return transaction {
            EmployeeTable.deleteAll()
        }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [EmployeeInput] model instance.
     */
    private fun employeeModelToTable(employee: EmployeeInput, target: UpdateBuilder<Int>, contactId: UUID) {
        target.apply {
            this[EmployeeTable.firstName] = employee.firstName.trim()
            this[EmployeeTable.lastName] = employee.lastName.trim()
            this[EmployeeTable.dob] = employee.dob
            this[EmployeeTable.contactId] = contactId
        }
    }
}
