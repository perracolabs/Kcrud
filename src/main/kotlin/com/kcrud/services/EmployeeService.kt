/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.services

import com.kcrud.data.models.employee.Employee
import com.kcrud.data.models.employee.EmployeeParams
import com.kcrud.data.repositories.employee.IEmployeeRepository
import org.koin.core.component.KoinComponent
import java.util.*

/**
 * Employee service, where all the employee business logic should be defined.
 * Currently, this service is only used to delegate calls to the repository.
 */
internal class EmployeeService(private val repository: IEmployeeRepository) : KoinComponent {

    /**
     * Retrieves as employee by its ID.
     * @param employeeId The ID of the employee to be retrieved.
     * @return The employee model if found, null otherwise.
     */
    fun findById(employeeId: UUID): Employee? {
        return repository.findById(employeeId)
    }

    /**
     * Retrieves all employees in the system.
     * @return List of all employee models.
     */
    fun findAll(): List<Employee> {
        return repository.findAll()
    }

    /**
     * Creates a new employee in the system.
     * @param employee The employee to be created.
     * @return The created employee model with generated ID.
     */
    fun create(employee: EmployeeParams): Employee {
        return repository.create(employee)
    }

    /**
     * Updates an employee's details in the system.
     * @param employeeId The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee model if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employee: EmployeeParams): Employee? {
        return repository.update(employeeId = employeeId, employee = employee)
    }

    /**
     * Deletes an employee from the system using the provided ID.
     * @param employeeId The ID of the employee to be deleted.
     * @return The number of deleted records.
     */
    fun delete(employeeId: UUID): Int {
        return repository.delete(employeeId)
    }

    /**
     * Deletes all employees from the system.
     * @return The number of deleted records.
     */
    fun deleteAll(): Int {
        return repository.deleteAll()
    }
}
