/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.repositories

import com.kcrud.data.models.Employee

interface IEmployeeRepository {

    /**
     * Retrieves an employee model by its ID from the database.
     * @param id The ID of the employee to be retrieved.
     * @return The employee model if found, null otherwise.
     */
    fun findById(id: Int): Employee?

    /**
     * Retrieves all employee models from the database.
     * @return List of all employee models.
     */
    fun findAll(): List<Employee>

    /**
     * Creates a new employee in the database and returns the created employee model.
     * @param employee The employee to be created.
     * @return The created employee model with generated ID.
     */
    fun create(employee: Employee): Employee

    /**
     * Updates an employee's details in the database using the provided ID and employee model.
     * @param id The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee model if the update was successful, null otherwise.
     */
    fun update(id: Int, employee: Employee): Employee?

    /**
     * Deletes an employee from the database using the provided ID.
     * @param id The ID of the employee to be deleted.
     * @return The number of deleted records.
     */
    fun delete(id: Int): Int

    /**
     * Deletes all employees from the database.
     * @return The number of deleted records.
     */
    fun deleteAll(): Int
}

