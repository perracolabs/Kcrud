/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee

import com.kcrud.data.database.tables.Contacts
import com.kcrud.data.database.tables.Employees
import com.kcrud.data.models.Employee
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class EmployeeRepository : IEmployeeRepository {

    override fun findById(employeeId: Int): Employee? {
        return transaction {
            val query = Employees.join(
                otherTable = Contacts,
                joinType = JoinType.INNER,
                onColumn = Employees.contactId,
                otherColumn = Contacts.id
            ).select { Employees.id eq employeeId }

            query.map { resultRow ->
                Employee.fromTableRow(row = resultRow)
            }.singleOrNull()
        }
    }

    override fun findAll(): List<Employee> {
        return transaction {
            val query = Employees.join(
                otherTable = Contacts,
                joinType = JoinType.INNER,
                onColumn = Employees.contactId,
                otherColumn = Contacts.id
            ).selectAll()

            query.map { resultRow ->
                Employee.fromTableRow(row = resultRow)
            }
        }
    }

    override fun create(employee: Employee): Employee {
        return transaction {
            val newContactId = Contacts.insert { contactRow ->
                contactRow[email] = employee.contact.email.trim()
                contactRow[phone] = employee.contact.phone.trim()
            } get Contacts.id

            employee.contact.id = newContactId

            val newEmployeeId = Employees.insert { employeeRow ->
                employeeModelToTable(employee = employee, target = employeeRow)
                employeeRow[contactId] = newContactId
            } get Employees.id

            findById(newEmployeeId)!!
        }
    }

    override fun update(employeeId: Int, employee: Employee): Employee? {
        return transaction {
            // First, find the existing employee to get the contact id.
            val targetEmployee = findById(employeeId) ?: return@transaction null
            val dbContactId = targetEmployee.contact.id!!
            employee.contact.id = dbContactId

            // Update the Contacts table.
            Contacts.update(where = { Contacts.id eq dbContactId }) { contactRow ->
                contactRow[email] = employee.contact.email.trim()
                contactRow[phone] = employee.contact.phone.trim()
            }

            // Update the Employees table.
            Employees.update(where = { Employees.id eq employeeId }) { employeeRow ->
                employeeModelToTable(employee = employee, target = employeeRow)
            }

            findById(employeeId)
        }
    }

    override fun delete(employeeId: Int): Int {
        return transaction {
            Employees.deleteWhere { id eq employeeId }
        }
    }

    override fun deleteAll(): Int {
        return transaction {
            Employees.deleteAll()
        }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [Employee] model instance.
     */
    private fun employeeModelToTable(employee: Employee, target: UpdateBuilder<Int>) {
        target.apply {
            this[Employees.firstName] = employee.firstName.trim()
            this[Employees.lastName] = employee.lastName.trim()
            this[Employees.dob] = employee.dob
            this[Employees.contactId] = employee.contact.id!!
        }
    }
}
