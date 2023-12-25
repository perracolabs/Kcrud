/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.contact

import com.kcrud.data.entities.contact.Contact
import com.kcrud.data.entities.contact.ContactRequest
import com.kcrud.data.entities.employee.EmployeeRequest
import java.util.*

interface IContactRepository {

    /**
     * Finds a contact by its id.
     */
    fun findById(contactId: UUID): Contact?

    /**
     * Finds a contact by the id of its employee.
     */
    fun findByEmployeeId(employeeId: UUID): Contact?

    /**
     * Sets the contact of an employee, either creating it, updating it
     * or deleting it depending on the request.
     *
     * @return The id of the contact if it was created or updated, null if it was deleted.
     */
    fun setByEmployee(employeeId: UUID, employeeRequest: EmployeeRequest): UUID?

    /**
     * Creates a new contact for an employee.
     */
    fun createContact(employeeId: UUID, contactRequest: ContactRequest): UUID

    /**
     * Updates the contact of an employee.
     *
     * @return The number of rows updated.
     */
    fun updateContact(employeeId: UUID, contactId: UUID, contactRequest: ContactRequest): Int

    /**
     * Deletes the contact of an employee.
     */
    fun deleteContact(contactId: UUID): Int

    /**
     * Deletes the contact of an employee.
     */
    fun deleteContactByEmployeeId(employeeId: UUID): Int
}