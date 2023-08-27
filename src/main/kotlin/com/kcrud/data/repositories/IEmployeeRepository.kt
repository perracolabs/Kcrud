package com.kcrud.data.repositories

import com.kcrud.data.models.EmployeeEntity
import com.kcrud.data.models.EmployeeInput
import com.kcrud.data.models.EmployeePatchDTO


interface IEmployeeRepository {

    /**
     * Creates a new employee in the database and returns the created employee entity.
     * @param employee The employee entity to be created.
     * @return The created employee entity with generated ID.
     */
    fun create(employee: EmployeeInput): EmployeeEntity

    /**
     * Retrieves an employee entity by its ID from the database.
     * @param id The ID of the employee to be retrieved.
     * @return The employee entity if found, null otherwise.
     */
    fun findById(id: Int): EmployeeEntity?

    /**
     * Retrieves all employee entities from the database.
     * @return List of all employee entities.
     */
    fun findAll(): List<EmployeeEntity>

    /**
     * Updates an employee's details in the database using the provided ID and employee entity.
     * @param id The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee entity if the update was successful, null otherwise.
     */
    fun update(id: Int, employee: EmployeeInput): EmployeeEntity?

    /**
     * Patches a given employee's details.
     * @param id The ID of the employee to be patched.
     * @param employeePatch The details to be applied.
     * @return The updated employee entity if the update was successful, null otherwise.
     */
    fun patch(id: Int, employeePatch: EmployeePatchDTO): EmployeeEntity?

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

