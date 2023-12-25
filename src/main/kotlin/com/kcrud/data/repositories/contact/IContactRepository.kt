/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.contact

import com.kcrud.data.entities.contact.Contact
import com.kcrud.data.entities.contact.ContactRequest
import java.util.*

interface IContactRepository {

    /**
     * Finds a contact by its id.
     */
    fun findById(contactId: UUID): Contact?

    /**
     * Either creates or updates the contact of an employee.
     */
    fun setContact(employeeId: UUID, contactId: UUID?, contactRequest: ContactRequest): UUID?

    /**
     * Creates a new contact for an employee.
     */
    fun createContact(employeeId: UUID, contactRequest: ContactRequest): UUID

    /**
     * Deletes the contact of an employee.
     */
    fun deleteContact(contactId: UUID): Int
}