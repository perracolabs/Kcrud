/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.services

import com.kcrud.data.models.employment.EmploymentInput
import com.kcrud.data.models.employment.Employment
import com.kcrud.data.repositories.employment.IEmploymentRepository
import org.koin.core.component.KoinComponent
import java.util.*

class EmploymentService(private val repository: IEmploymentRepository) : KoinComponent {

    /**
     * Retrieves an employment model by its ID.
     * @param employmentId The ID of the employment to be retrieved.
     * @return The employment model if found, null otherwise.
     */
    fun findById(employmentId: UUID): Employment? {
        return repository.findById(employmentId)
    }

    /**
     * Retrieves all employment models for a given employee.
     * @param employeeId The ID of the employee associated with the employment.
     * @return List of all employment models.
     */
    fun findByEmployeeId(employeeId: UUID): List<Employment> {
        return repository.findByEmployeeId(employeeId = employeeId)
    }

    /**
     * Creates a new employment and returns the created employment model.
     * @param employeeId The employee ID associated with the employment.
     * @param employment The employment to be created.
     * @return The created employment model with generated ID.
     */
    fun create(employeeId: UUID, employment: EmploymentInput): Employment {
        return repository.create(employeeId = employeeId, employment = employment)
    }

    /**
     * Updates an employment's details using the provided ID and employment model.
     * @param employeeId The employee ID associated with the employment.
     * @param employmentId The ID of the employment to be updated.
     * @param employment The new details for the employment.
     * @return The updated employment model if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employmentId: UUID, employment: EmploymentInput): Employment? {
        return repository.update(employeeId = employeeId, employmentId = employmentId, employment = employment)
    }

    /**
     * Deletes an employment using the provided ID.
     * @param employmentId The ID of the employment to be deleted.
     * @return The number of deleted records.
     */
    fun delete(employmentId: UUID): Int {
        return repository.delete(employmentId)
    }
}
