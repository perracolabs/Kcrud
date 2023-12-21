/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories.employee

import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.entities.employee.EmployeeParams
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
     * @return List of all employee entities.
     */
    fun findAll(): List<Employee>

    /**
     * Creates a new employee and returns the created employee entity.
     * @param employee The employee to be created.
     * @return The created employee entity with generated ID.
     */
    fun create(employee: EmployeeParams): Employee

    /**
     * Updates an employee's details using the provided ID and employee entity.
     * @param employeeId The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee entity if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employee: EmployeeParams): Employee?

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

