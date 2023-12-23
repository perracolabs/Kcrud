/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.entities.employee.EmployeeParams
import com.kcrud.data.pagination.Page
import com.kcrud.data.pagination.Pageable
import com.kcrud.data.pagination.applyPagination
import com.kcrud.data.repositories.employee.types.EmployeeFilterSet
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class EmployeeRepository : IEmployeeRepository {

    private fun getFindEmployeeJoin(): Join {
        return EmployeeTable.join(
            otherTable = ContactTable,
            joinType = JoinType.LEFT,
            onColumn = EmployeeTable.id,
            otherColumn = ContactTable.employeeId
        )
    }

    override fun findById(employeeId: UUID): Employee? {
        return transaction {
            getFindEmployeeJoin().select {
                EmployeeTable.id eq employeeId
            }.singleOrNull()?.let { resultRow ->
                Employee.toEntity(row = resultRow)
            }
        }
    }

    override fun findAll(pageable: Pageable?): Page<Employee> {
        return transaction {
            val query = getFindEmployeeJoin().selectAll()
            val totalElements = query.count()

            val paginatedData = query
                .applyPagination(pageable = pageable)
                .map { resultRow ->
                    Employee.toEntity(row = resultRow)
                }

            Page.create(content = paginatedData, totalElements = totalElements, pageable = pageable)
        }
    }

    override fun filter(filterSet: EmployeeFilterSet, pageable: Pageable?): Page<Employee> {
        return transaction {
            val query = EmployeeTable.selectAll()
            val totalElements = query.count()

            // Using lowerCase() to make the search case-insensitive.
            // This could be removed if the database is configured to use a case-insensitive collation.

            filterSet.firstName?.let { firstName ->
                query.andWhere { EmployeeTable.firstName.lowerCase() like "%${firstName.lowercase()}%" }
            }
            filterSet.lastName?.let { lastName ->
                query.andWhere { EmployeeTable.lastName.lowerCase() like "%${lastName.lowercase()}%" }
            }
            filterSet.honorific?.let { honorificList ->
                if (honorificList.isNotEmpty()) {
                    query.andWhere { EmployeeTable.honorific inList honorificList }
                }
            }
            filterSet.maritalStatus?.let { maritalStatusList ->
                if (maritalStatusList.isNotEmpty()) {
                    query.andWhere { EmployeeTable.maritalStatus inList maritalStatusList }
                }
            }

            val paginatedData = query
                .applyPagination(pageable = pageable)
                .map { resultRow ->
                    Employee.toEntity(row = resultRow)
                }

            Page.create(content = paginatedData, totalElements = totalElements, pageable = pageable)
        }
    }

    override fun create(employee: EmployeeParams): Employee {
        return transaction {
            val newEmployeeId = EmployeeTable.insert { employeeRow ->
                employeeParamsToTable(employee = employee, target = employeeRow)
            } get EmployeeTable.id

            employee.contact?.let {
                ContactTable.insert { contactRow ->
                    contactRow[employeeId] = newEmployeeId
                    contactRow[email] = employee.contact.email.trim()
                    contactRow[phone] = employee.contact.phone.trim()
                } get ContactTable.id
            }

            findById(newEmployeeId)!!
        }
    }

    override fun update(employeeId: UUID, employee: EmployeeParams): Employee? {
        return transaction {
            // First, find the existing employee to get the contact id.
            val dbEmployee = findById(employeeId) ?: return@transaction null

            employee.contact?.let {
                if (dbEmployee.contact == null) {
                    // If the employee doesn't have a contact, create a new one.
                    ContactTable.insert { contactRow ->
                        contactRow[email] = employee.contact.email.trim()
                        contactRow[phone] = employee.contact.phone.trim()
                    } get ContactTable.id
                } else {
                    // If the employee already has a contact, update it.
                    ContactTable.update(where = { ContactTable.id eq dbEmployee.contact.id }) { contactRow ->
                        contactRow[email] = employee.contact.email.trim()
                        contactRow[phone] = employee.contact.phone.trim()
                    }
                    dbEmployee.contact.id
                }
            }

            // Update the Employees table.
            EmployeeTable.update(where = { EmployeeTable.id eq employeeId }) { employeeRow ->
                employeeParamsToTable(employee = employee, target = employeeRow)
            }

            findById(employeeId)
        }
    }

    override fun delete(employeeId: UUID): Int {
        return transaction {
            deleteRelatedRecords(employeeId)
            EmployeeTable.deleteWhere { id eq employeeId }
        }
    }

    override fun deleteAll(): Int {
        return transaction {
            deleteRelatedRecords()
            EmployeeTable.deleteAll()
        }
    }

    /**
     * Although related tables are configured for cascade deletion,
     * seems H2 and Sqlite don't support it, so delete related records manually.
     *
     * Passing a null employee id means to delete all records.
     */
    private fun deleteRelatedRecords(employeeId: UUID? = null) {
        if (employeeId == null) {
            EmploymentTable.deleteAll()
            ContactTable.deleteAll()
        } else {
            EmploymentTable.deleteWhere { EmploymentTable.employeeId eq employeeId }
            ContactTable.deleteWhere { ContactTable.employeeId eq employeeId }
        }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from an [EmployeeParams] instance.
     */
    private fun employeeParamsToTable(employee: EmployeeParams, target: UpdateBuilder<Int>) {
        target.apply {
            this[EmployeeTable.firstName] = employee.firstName.trim()
            this[EmployeeTable.lastName] = employee.lastName.trim()
            this[EmployeeTable.dob] = employee.dob
            this[EmployeeTable.maritalStatus] = employee.maritalStatus
            this[EmployeeTable.honorific] = employee.honorific
        }
    }
}
