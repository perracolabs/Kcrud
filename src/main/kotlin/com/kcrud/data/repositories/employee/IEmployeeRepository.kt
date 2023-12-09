/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee

import com.kcrud.data.models.employee.EmployeeInput
import com.kcrud.data.models.employee.Employee
import java.util.*

interface IEmployeeRepository {

    /**
     * Retrieves an employee model by its ID.
     * @param employeeId The ID of the employee to be retrieved.
     * @return The employee model if found, null otherwise.
     */
    fun findById(employeeId: UUID): Employee?

    /**
     * Retrieves all employee models.
     * @return List of all employee models.
     */
    fun findAll(): List<Employee>

    /**
     * Creates a new employee and returns the created employee model.
     * @param employee The employee to be created.
     * @return The created employee model with generated ID.
     */
    fun create(employee: EmployeeInput): Employee

    /**
     * Updates an employee's details using the provided ID and employee model.
     * @param employeeId The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee model if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employee: EmployeeInput): Employee?

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

