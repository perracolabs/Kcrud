/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.services

import com.kcrud.data.entities.employee.Employee
import com.kcrud.data.entities.employee.EmployeeParams
import com.kcrud.data.database.shared.Pagination
import com.kcrud.data.repositories.employee.EmployeeFilterSet
import com.kcrud.data.repositories.employee.IEmployeeRepository
import org.koin.core.component.KoinComponent
import java.util.*

/**
 * Employee service, where all the employee business logic should be defined.
 * Currently, this service is only used to delegate calls to the repository.
 */
class EmployeeService(private val repository: IEmployeeRepository) : KoinComponent {

    /**
     * Retrieves as employee by its ID.
     * @param employeeId The ID of the employee to be retrieved.
     * @return The employee entity if found, null otherwise.
     */
    fun findById(employeeId: UUID): Employee? {
        return repository.findById(employeeId = employeeId)
    }

    /**
     * Retrieves all employees in the system.
     * @return List of all employee entities.
     */
    fun findAll(pagination: Pagination?): List<Employee> {
        return repository.findAll(pagination = pagination)
    }

    /**
     * Retrieves all employees in the system that match the provided filter set.
     * @param filterSet The filter set to be applied.
     * @param pagination The pagination options to be applied.
     * @return List of all employee entities that match the provided filter set.
     */
    fun filter(filterSet: EmployeeFilterSet, pagination: Pagination): List<Employee> {
        return repository.filter(filterSet = filterSet, pagination = pagination)
    }

    /**
     * Creates a new employee in the system.
     * @param employee The employee to be created.
     * @return The created employee entity with generated ID.
     */
    fun create(employee: EmployeeParams): Employee {
        return repository.create(employee = employee)
    }

    /**
     * Updates an employee's details in the system.
     * @param employeeId The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee entity if the update was successful, null otherwise.
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
        return repository.delete(employeeId = employeeId)
    }

    /**
     * Deletes all employees from the system.
     * @return The number of deleted records.
     */
    fun deleteAll(): Int {
        return repository.deleteAll()
    }
}
