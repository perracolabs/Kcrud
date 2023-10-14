/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employment

import com.kcrud.data.models.Employment

interface IEmploymentRepository {

    /**
     * Retrieves an employment model by its ID.
     * @param employmentId The ID of the employment to be retrieved.
     * @return The employment model if found, null otherwise.
     */
    fun findById(employmentId: Int): Employment?

    /**
     * Retrieves all employment models for a given employee.
     * @param employeeId The ID of the employee associated with the employment.
     * @return List of all employment models.
     */
    fun findByEmployeeId(employeeId: Int): List<Employment>

    /**
     * Creates a new employment and returns the created employment model.
     * @param employeeId The employee ID associated with the employment.
     * @param employment The employment to be created.
     * @return The created employment model with generated ID.
     */
    fun create(employeeId: Int, employment: Employment): Employment

    /**
     * Updates an employment's details using the provided ID and employment model.
     * @param employeeId The employee ID associated with the employment.
     * @param employmentId The ID of the employment to be updated.
     * @param employment The new details for the employment.
     * @return The updated employment model if the update was successful, null otherwise.
     */
    fun update(employeeId: Int, employmentId: Int, employment: Employment): Employment?

    /**
     * Deletes an employment using the provided ID.
     * @param employmentId The ID of the employment to be deleted.
     * @return The number of deleted records.
     */
    fun delete(employmentId: Int): Int
}
