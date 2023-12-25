/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.contact

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.entities.contact.Contact
import com.kcrud.data.entities.contact.ContactRequest
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


    override fun setContact(employeeId: UUID, contactId: UUID?, contactRequest: ContactRequest): UUID? {
        return transaction {
            if (contactId == null) {
                createContact(employeeId = employeeId, contactRequest = contactRequest)
            } else {
                updateContact(
                    employeeId = employeeId,
                    contactId = contactId,
                    contactRequest = contactRequest
                )
            }
        }
    }

    override fun createContact(employeeId: UUID, contactRequest: ContactRequest): UUID {
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

    /**
     * Updates the contact of an employee.
     */
    private fun updateContact(employeeId: UUID, contactId: UUID, contactRequest: ContactRequest): UUID? {
        return transaction {
            val updatedCount = ContactTable.update(where = { ContactTable.id eq contactId }) { contactRow ->
                contactRequestToTable(
                    employeeId = employeeId,
                    contactRequest = contactRequest,
                    target = contactRow
                )
            }

            contactId.takeIf { updatedCount > 0 }
        }
    }

    override fun deleteContact(contactId: UUID): Int {
        return ContactTable.deleteWhere { id eq contactId }
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