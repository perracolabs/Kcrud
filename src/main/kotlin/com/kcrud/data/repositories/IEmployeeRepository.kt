package com.kcrud.data.repositories

import com.kcrud.data.models.EmployeeEntity


interface IEmployeeRepository {

    /**
     * Creates a new employee in the database and returns the created employee entity.
     * @param employee The employee entity to be created.
     * @return The created employee entity with generated ID.
     */
    fun create(employee: EmployeeEntity): EmployeeEntity

    /**
     * Retrieves a employee entity by its ID from the database.
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
     * Updates a employee's details in the database using the provided ID and employee entity.
     * @param id The ID of the employee to be updated.
     * @param employee The new details for the employee.
     * @return The updated employee entity if the update was successful, null otherwise.
     */
    fun update(id: Int, employee: EmployeeEntity): EmployeeEntity?

    /**
     * Deletes a employee from the database using the provided ID.
     * @param id The ID of the employee to be deleted.
     */
    fun delete(id: Int)

    /**
     * Deletes all employees from the database.
     */
    fun deleteAll()
}

