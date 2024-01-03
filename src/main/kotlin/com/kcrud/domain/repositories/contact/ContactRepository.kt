/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.domain.repositories.contact

import com.kcrud.data.tables.ContactTable
import com.kcrud.domain.entities.contact.Contact
import com.kcrud.domain.entities.contact.ContactRequest
import com.kcrud.domain.entities.employee.EmployeeRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

internal class ContactRepository : IContactRepository {

    override fun findById(contactId: UUID): Contact? {
        return transaction {
            ContactTable.select {
                ContactTable.id eq contactId
            }.singleOrNull()?.let { resultRow ->
                Contact.toEntity(row = resultRow)
            }
        }
    }

    override fun findByEmployeeId(employeeId: UUID): Contact? {
        return transaction {
            ContactTable.select {
                ContactTable.employeeId eq employeeId
            }.singleOrNull()?.let { resultRow ->
                Contact.toEntity(row = resultRow)
            }
        }
    }

    override fun setByEmployee(employeeId: UUID, employeeRequest: EmployeeRequest): UUID? {
        return if (employeeRequest.contact == null) {
            deleteByEmployeeId(employeeId = employeeId)
            null
        } else {
            val contactId: UUID? = findByEmployeeId(employeeId = employeeId)?.id

            if (contactId == null) {
                create(employeeId = employeeId, contactRequest = employeeRequest.contact)
            } else {
                val updateCount = update(
                    employeeId = employeeId,
                    contactId = contactId,
                    contactRequest = employeeRequest.contact
                )

                contactId.takeIf { updateCount > 0 }
            }
        }
    }

    override fun create(employeeId: UUID, contactRequest: ContactRequest): UUID {
        return transaction {
            ContactTable.insert { contactRow ->
                contactRequestToTable(
                    employeeId = employeeId,
                    contactRequest = contactRequest,
                    target = contactRow
                )
            } get ContactTable.id
        }
    }

    override fun update(employeeId: UUID, contactId: UUID, contactRequest: ContactRequest): Int {
        return transaction {
            ContactTable.update(where = { ContactTable.id eq contactId }) { contactRow ->
                contactRequestToTable(
                    employeeId = employeeId,
                    contactRequest = contactRequest,
                    target = contactRow
                )
            }
        }
    }

    override fun delete(contactId: UUID): Int {
        return ContactTable.deleteWhere { id eq contactId }
    }

    override fun deleteByEmployeeId(employeeId: UUID): Int {
        return ContactTable.deleteWhere { ContactTable.employeeId eq employeeId }
    }

    /**
     * Populates an SQL [UpdateBuilder] with data from a [ContactRequest] instance.
     */
    private fun contactRequestToTable(employeeId: UUID, contactRequest: ContactRequest, target: UpdateBuilder<Int>) {
        target.apply {
            this[ContactTable.employeeId] = employeeId
            this[ContactTable.email] = contactRequest.email.trim()
            this[ContactTable.phone] = contactRequest.phone.trim()
        }
    }
}