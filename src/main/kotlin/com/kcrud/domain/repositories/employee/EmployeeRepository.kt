/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.domain.repositories.employee

import com.kcrud.data.tables.ContactTable
import com.kcrud.data.tables.EmployeeTable
import com.kcrud.data.tables.EmploymentTable
import com.kcrud.data.utils.pagination.Page
import com.kcrud.data.utils.pagination.Pageable
import com.kcrud.data.utils.pagination.applyPagination
import com.kcrud.domain.entities.employee.Employee
import com.kcrud.domain.entities.employee.EmployeeFilterSet
import com.kcrud.domain.entities.employee.EmployeeRequest
import com.kcrud.domain.repositories.contact.IContactRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal class EmployeeRepository(private val contactRepository: IContactRepository) : IEmployeeRepository {

    override fun findById(employeeId: UUID): Employee? {
        return transaction {
            EmployeeTable.join(
                otherTable = ContactTable,
                joinType = JoinType.LEFT,
                onColumn = EmployeeTable.id,
                otherColumn = ContactTable.employeeId
            ).select {
                EmployeeTable.id eq employeeId
            }.singleOrNull()?.let { resultRow ->
                Employee.toEntity(row = resultRow)
            }
        }
    }

    override fun findAll(pageable: Pageable?): Page<Employee> {
        return transaction {
            val query: Query = EmployeeTable.join(
                otherTable = ContactTable,
                joinType = JoinType.LEFT,
                onColumn = EmployeeTable.id,
                otherColumn = ContactTable.employeeId
            ).selectAll()

            val totalElements: Long = query.count()

            val paginatedData: List<Employee> = query
                .applyPagination(pageable = pageable)
                .map { resultRow ->
                    Employee.toEntity(row = resultRow)
                }

            Page.build(content = paginatedData, totalElements = totalElements, pageable = pageable)
        }
    }

    override fun filter(filterSet: EmployeeFilterSet, pageable: Pageable?): Page<Employee> {
        return transaction {
            val query: Query = EmployeeTable.selectAll()

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

            // Count total elements after applying filters.
            val totalFilteredElements: Long = query.count()

            val paginatedData: List<Employee> = query
                .applyPagination(pageable = pageable)
                .map { resultRow ->
                    Employee.toEntity(row = resultRow)
                }

            Page.build(content = paginatedData, totalElements = totalFilteredElements, pageable = pageable)
        }
    }

    override fun create(employeeRequest: EmployeeRequest): UUID {
        return transaction {
            val newEmployeeId: UUID = EmployeeTable.insert { employeeRow ->
                employeeRequestToTable(employeeRequest = employeeRequest, target = employeeRow)
            } get EmployeeTable.id

            employeeRequest.contact?.let {
                contactRepository.create(
                    employeeId = newEmployeeId,
                    contactRequest = employeeRequest.contact
                )
            }

            newEmployeeId
        }
    }

    override fun update(employeeId: UUID, employeeRequest: EmployeeRequest): Int {
        return transaction {

            val updateCount: Int = EmployeeTable.update(
                where = { EmployeeTable.id eq employeeId }
            ) { employeeRow ->
                employeeRequestToTable(employeeRequest = employeeRequest, target = employeeRow)
            }

            if (updateCount > 0) {
                contactRepository.setByEmployee(
                    employeeId = employeeId,
                    employeeRequest = employeeRequest
                )
            }

            updateCount
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
     * Populates an SQL [UpdateBuilder] with data from an [EmployeeRequest] instance.
     */
    private fun employeeRequestToTable(employeeRequest: EmployeeRequest, target: UpdateBuilder<Int>) {
        target.apply {
            this[EmployeeTable.firstName] = employeeRequest.firstName.trim()
            this[EmployeeTable.lastName] = employeeRequest.lastName.trim()
            this[EmployeeTable.dob] = employeeRequest.dob
            this[EmployeeTable.maritalStatus] = employeeRequest.maritalStatus
            this[EmployeeTable.honorific] = employeeRequest.honorific
        }
    }
}
