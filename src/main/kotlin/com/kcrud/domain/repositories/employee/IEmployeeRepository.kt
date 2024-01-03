/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.domain.repositories.employee

import com.kcrud.data.utils.pagination.Page
import com.kcrud.data.utils.pagination.Pageable
import com.kcrud.domain.entities.employee.Employee
import com.kcrud.domain.entities.employee.EmployeeFilterSet
import com.kcrud.domain.entities.employee.EmployeeRequest
import java.util.*

interface IEmployeeRepository {

    /**
     * Retrieves an employee entity by its ID.
     * @param employeeId The ID of the employee to be retrieved.
     * @return The employee entity if found, null otherwise.
     */
    fun findById(employeeId: UUID): Employee?

    /**
     * Retrieves all employee entities.
     * @param pageable The pagination options to be applied.
     *                 If not provided, a single page with the result will be returned.
     * @return List of all employee entities.
     */
    fun findAll(pageable: Pageable? = null): Page<Employee>

    /**
     * Retrieves all employee entities matching the provided filter set.
     * @param filterSet The filter set to be applied.
     * @param pageable The pagination options to be applied.
     *                 If not provided, a single page with the result will be returned.
     * @return List of all employee entities matching the provided filter set.
     */
    fun filter(filterSet: EmployeeFilterSet, pageable: Pageable? = null): Page<Employee>

    /**
     * Creates a new employee and returns the created employee entity.
     * @param employeeRequest The employee to be created.
     * @return The created employee ID.
     */
    fun create(employeeRequest: EmployeeRequest): UUID

    /**
     * Updates an employee's details using the provided ID and employee entity.
     * @param employeeId The ID of the employee to be updated.
     * @param employeeRequest The new details for the employee.
     * @return How many records were updated.
     */
    fun update(employeeId: UUID, employeeRequest: EmployeeRequest): Int

    /**
     * Deletes an employee using the provided ID.
     * @param employeeId The ID of the employee to be deleted.
     * @return The number of deleted records.
     */
    fun delete(employeeId: UUID): Int

    /**
     * Deletes all employees.
     * @return The number of deleted records.
     */
    fun deleteAll(): Int
}

