/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.services

import com.kcrud.data.entities.EmployeeEntity
import com.kcrud.data.repositories.IEmployeeRepository
import org.koin.core.component.KoinComponent

class EmployeeService(private val repository: IEmployeeRepository) : KoinComponent {

    /**
     * Creates a new employee in the system.
     * @param employee The employee entity to be created.
     * @return The created employee entity with generated ID.
     */
    fun create(employee: EmployeeEntity): EmployeeEntity {
        return repository.create(employee)
    }

    /**
     * Retrieves as employee by its ID.
     * @param id The ID of the employee to be retrieved.
     * @return The employee entity if found, null otherwise.
     */
    fun findById(id: Int): EmployeeEntity? {
        return repository.findById(id)
    }

    /**
     * Retrieves all employees in the system.
     * @return List of all employee entities.
     */
    fun findAll(): List<EmployeeEntity> {
        return repository.findAll()
    }

    /**
     * Updates an employee's details in the system.
     * @param id The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee entity if the update was successful, null otherwise.
     */
    fun update(id: Int, employee: EmployeeEntity): EmployeeEntity? {
        return repository.update(id, employee)
    }

    /**
     * Deletes an employee from the system using the provided ID.
     * @param id The ID of the employee to be deleted.
     */
    fun delete(id: Int) {
        repository.delete(id)
    }

    /**
     * Deletes all employees from the system.
     */
    fun deleteAll() {
        repository.deleteAll()
    }
}
