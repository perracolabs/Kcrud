/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.domain.repositories.contact

import com.kcrud.domain.entities.contact.Contact
import com.kcrud.domain.entities.contact.ContactRequest
import com.kcrud.domain.entities.employee.EmployeeRequest
import java.util.*

interface IContactRepository {

    /**
     * Finds a contact by its id.
     *
     * @param contactId The id of the contact to be retrieved.
     * @return The contact if it exists, null otherwise.
     */
    fun findById(contactId: UUID): Contact?

    /**
     * Finds a contact by the id of its employee.
     *
     * @param employeeId The id of the employee whose contact is to be retrieved.
     * @return The contact of the employee if it exists, null otherwise.
     */
    fun findByEmployeeId(employeeId: UUID): Contact?

    /**
     * Sets the contact of an employee, either creating it, updating it
     * or deleting it depending on the request.
     *
     * @param employeeId The id of the employee to set the contact for.
     * @param employeeRequest The details of the employee to be processed.
     * @return The id of the contact if it was created or updated, null if it was deleted.
     */
    fun setByEmployee(employeeId: UUID, employeeRequest: EmployeeRequest): UUID?

    /**
     * Creates a new contact for an employee.
     *
     * @param employeeId The id of the employee to create the contact for.
     * @param contactRequest The details of the contact to be created.
     * @return The id of the created contact.
     */
    fun create(employeeId: UUID, contactRequest: ContactRequest): UUID

    /**
     * Updates the contact of an employee.
     *
     * @param contactId The id of the contact to be updated.
     * @param contactRequest The new details for the contact.
     * @return The number of rows updated.
     */
    fun update(employeeId: UUID, contactId: UUID, contactRequest: ContactRequest): Int

    /**
     * Deletes the contact of an employee.
     *
     * @param contactId The id of the contact to be deleted.
     * @return The number of rows deleted.
     */
    fun delete(contactId: UUID): Int

    /**
     * Deletes the contact of an employee.
     *
     * @param employeeId The id of the employee whose contact is to be deleted.
     * @return The number of rows deleted.
     */
    fun deleteByEmployeeId(employeeId: UUID): Int
}