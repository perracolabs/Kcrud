/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories

import com.kcrud.data.models.Contact
import com.kcrud.data.models.Employee
import com.kcrud.data.database.tables.Contacts
import com.kcrud.data.database.tables.Employees
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

class EmployeeRepository : IEmployeeRepository {

    override fun findById(id: Int): Employee? {
        return transaction {
            val query = Employees.join(
                otherTable = Contacts,
                joinType = JoinType.INNER,
                onColumn = Employees.contactId,
                otherColumn = Contacts.id
            ).select { Employees.id eq id }

            query.map { row ->
                rowToEmployeeModel(row = row)
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

            query.map { row ->
                rowToEmployeeModel(row = row)
            }
        }
    }

    override fun create(employee: Employee): Employee {
        return transaction {
            val newContactId = Contacts.insert { dbContact ->
                dbContact[email] = employee.contact.email.trim()
                dbContact[phone] = employee.contact.phone.trim()
            } get Contacts.id

            employee.contact.id = newContactId

            val newEmployeeId = Employees.insert { dbEmployee ->
                employeeModelToStatement(employee = employee, dbStatement = dbEmployee)
                dbEmployee[contactId] = newContactId
            } get Employees.id

            findById(id = newEmployeeId)!!
        }
    }

    override fun update(id: Int, employee: Employee): Employee? {
        return transaction {
            // First, find the existing employee to get the contact id.
            val currentEmployee = findById(id) ?: return@transaction null
            val dbContactId = currentEmployee.contact.id!!
            employee.contact.id = dbContactId

            // Update the Contacts table.
            Contacts.update(where = { Contacts.id eq dbContactId }) { dbContact ->
                dbContact[email] = employee.contact.email.trim()
                dbContact[phone] = employee.contact.phone.trim()
            }

            // Update the Employees table.
            Employees.update(where = { Employees.id eq id }) { dbEmployee ->
                employeeModelToStatement(employee = employee, dbStatement = dbEmployee)
            }

            findById(id = id)
        }
    }

    override fun delete(id: Int): Int {
        return transaction {
            Employees.deleteWhere { Employees.id eq id }
        }
    }

    override fun deleteAll(): Int {
        return transaction {
            Employees.deleteAll()
        }
    }

    /**
     * Converts a database [ResultRow] to an [Employee] model instance.
     */
    private fun rowToEmployeeModel(row: ResultRow): Employee {
        return Employee(
            id = row[Employees.id],
            firstName = row[Employees.firstName],
            lastName = row[Employees.lastName],
            dob = row[Employees.dob],
            contact = Contact(
                id = row[Contacts.id],
                email = row[Contacts.email],
                phone = row[Contacts.phone]
            )
        )
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [Employee] model instance.
     */
    private fun employeeModelToStatement(employee: Employee, dbStatement: UpdateBuilder<Int>) {
        with(dbStatement) {
            this[Employees.firstName] = employee.firstName.trim()
            this[Employees.lastName] = employee.lastName.trim()
            this[Employees.dob] = employee.dob
            this[Employees.contactId] = employee.contact.id!!
        }
    }
}
