/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employment

import com.kcrud.data.entities.employment.Employment
import com.kcrud.data.entities.employment.EmploymentParams
import java.util.*

interface IEmploymentRepository {

    /**
     * Retrieves an employment entity by its ID.
     * @param employeeId The ID of the employee associated with the employment.
     * @param employmentId The ID of the employment to be retrieved.
     * @return The employment entity if found, null otherwise.
     */
    fun findById(employeeId: UUID, employmentId: UUID): Employment?

    /**
     * Retrieves all employment entities for a given employee.
     * @param employeeId The ID of the employee associated with the employment.
     * @return List of all employment entities.
     */
    fun findByEmployeeId(employeeId: UUID): List<Employment>

    /**
     * Creates a new employment and returns the created employment entity.
     * @param employeeId The employee ID associated with the employment.
     * @param employment The employment to be created.
     * @return The created employment entity with generated ID.
     */
    fun create(employeeId: UUID, employment: EmploymentParams): Employment

    /**
     * Updates an employment's details using the provided ID and employment entity.
     * @param employeeId The employee ID associated with the employment.
     * @param employmentId The ID of the employment to be updated.
     * @param employment The new details for the employment.
     * @return The updated employment entity if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employmentId: UUID, employment: EmploymentParams): Employment?

    /**
     * Deletes an employment using the provided ID.
     * @param employmentId The ID of the employment to be deleted.
     * @return The number of deleted records.
     */
    fun delete(employmentId: UUID): Int

    /**
     * Deletes all an employments for the given employee ID.
     * @param employeeId The ID of the employee to delete all its employments.
     * @return The number of deleted records.
     */
    fun deleteAll(employeeId: UUID): Int
}

