/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.services

import kcrud.server.domain.entities.employment.Employment
import kcrud.server.domain.entities.employment.EmploymentRequest
import kcrud.server.domain.exceptions.EmploymentError
import kcrud.server.domain.repositories.employment.IEmploymentRepository
import org.koin.core.component.KoinComponent
import java.util.*

/**
 * Employment service, where all the employment business logic should be defined.
 * Currently, this service is only used to delegate calls to the repository.
 */
internal class EmploymentService(private val repository: IEmploymentRepository) : KoinComponent {

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
     * @param employmentRequest The employment to be created.
     * @return The created employment entity with generated ID.
     */
    fun create(employeeId: UUID, employmentRequest: EmploymentRequest): Employment {
        verify(
            employeeId = employeeId,
            employmentId = null,
            employmentRequest = employmentRequest,
            reason = "Create Employment."
        )
        val employmentId: UUID = repository.create(employeeId = employeeId, employmentRequest = employmentRequest)
        return findById(employeeId = employeeId, employmentId = employmentId)!!
    }

    /**
     * Updates an employment's details using the provided ID and employment entity.
     * @param employmentId The ID of the employment to be updated.
     * @param employmentRequest The new details for the employment.
     * @return The updated employment entity if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employmentId: UUID, employmentRequest: EmploymentRequest): Employment? {
        verify(
            employeeId = employeeId,
            employmentId = employmentId,
            employmentRequest = employmentRequest,
            reason = "Update Employment."
        )

        val updateCount = repository.update(
            employeeId = employeeId,
            employmentId = employmentId,
            employmentRequest = employmentRequest
        )

        return if (updateCount > 0) {
            findById(employeeId = employeeId, employmentId = employmentId)
        } else {
            null
        }
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

    private fun verify(employeeId: UUID, employmentId: UUID?, employmentRequest: EmploymentRequest, reason: String) {
        // Verify that the employment period dates are valid.
        employmentRequest.period.endDate?.let { endDate ->
            if (endDate < employmentRequest.period.startDate) {
                EmploymentError.PeriodDatesMismatch(
                    employeeId = employeeId,
                    employmentId = employmentId,
                    startDate = employmentRequest.period.startDate,
                    endDate = endDate
                ).raise(reason = reason)
            }
        }

        // Verify that the employment probation end date is valid.
        employmentRequest.probationEndDate?.let { probationEndDate ->
            if (probationEndDate < employmentRequest.period.startDate) {
                EmploymentError.InvalidProbationEndDate(
                    employeeId = employeeId,
                    employmentId = employmentId,
                    startDate = employmentRequest.period.startDate,
                    probationEndDate = probationEndDate
                ).raise(reason = reason)
            }
        }
    }
}
