/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.services

import com.kcrud.data.entities.employment.Employment
import com.kcrud.data.entities.employment.EmploymentParams
import com.kcrud.data.repositories.employment.IEmploymentRepository
import org.koin.core.component.KoinComponent
import java.util.*

/**
 * Employment service, where all the employment business logic should be defined.
 * Currently, this service is only used to delegate calls to the repository.
 */
class EmploymentService(private val repository: IEmploymentRepository) : KoinComponent {

    /**
     * Retrieves an employment entity by its ID.
     * @param employeeId The ID of the employee associated with the employment.
     * @param employmentId The ID of the employment to be retrieved.
     * @return The employment entity if found, null otherwise.
     */
    fun findById(employeeId: UUID, employmentId: UUID): Employment? {
        return repository.findById(employeeId = employeeId, employmentId = employmentId)
    }

    /**
     * Retrieves all employment entities for a given employee.
     * @param employeeId The ID of the employee associated with the employment.
     * @return List of all employment entities.
     */
    fun findByEmployeeId(employeeId: UUID): List<Employment> {
        return repository.findByEmployeeId(employeeId = employeeId)
    }

    /**
     * Creates a new employment and returns the created employment entity.
     * @param employeeId The employee ID associated with the employment.
     * @param employment The employment to be created.
     * @return The created employment entity with generated ID.
     */
    fun create(employeeId: UUID, employment: EmploymentParams): Employment {
        return repository.create(employeeId = employeeId, employment = employment)
    }

    /**
     * Updates an employment's details using the provided ID and employment entity.
     * @param employmentId The ID of the employment to be updated.
     * @param employment The new details for the employment.
     * @return The updated employment entity if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employmentId: UUID, employment: EmploymentParams): Employment? {
        return repository.update(employeeId = employeeId, employmentId = employmentId, employment = employment)
    }

    /**
     * Deletes an employment using the provided ID.
     * @param employmentId The ID of the employment to be deleted.
     * @return The number of deleted records.
     */
    fun delete(employmentId: UUID): Int {
        return repository.delete(employmentId = employmentId)
    }

    /**
     * Deletes all an employments for the given employee ID.
     * @param employeeId The ID of the employee to delete all its employments.
     * @return The number of deleted records.
     */
    fun deleteAll(employeeId: UUID): Int {
        return repository.deleteAll(employeeId = employeeId)
    }
}
