/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.services

import kcrud.base.admin.env.security.utils.SecurityUtils
import kcrud.base.data.pagination.Page
import kcrud.base.data.pagination.Pageable
import kcrud.base.exceptions.shared.BaseError
import kcrud.server.domain.entities.employee.Employee
import kcrud.server.domain.entities.employee.EmployeeFilterSet
import kcrud.server.domain.entities.employee.EmployeeRequest
import kcrud.server.domain.exceptions.EmployeeError
import kcrud.server.domain.repositories.employee.IEmployeeRepository
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
     * @return The employee entity if found, null otherwise.
     */
    fun findById(employeeId: UUID): Employee? {
        return repository.findById(employeeId = employeeId)
    }

    /**
     * Retrieves all employees in the system.
     * @param pageable The pagination options to be applied.
     *                If not provided, a single page with the result will be returned.
     * @return List of all employee entities.
     */
    fun findAll(pageable: Pageable? = null): Page<Employee> {
        return repository.findAll(pageable = pageable)
    }

    /**
     * Retrieves all employees in the system that match the provided filter set.
     * @param filterSet The filter set to be applied.
     * @param pageable The pagination options to be applied.
     *                 If not provided, a single page with the result will be returned.
     * @return List of all employee entities that match the provided filter set.
     */
    fun filter(filterSet: EmployeeFilterSet, pageable: Pageable? = null): Page<Employee> {
        return repository.filter(filterSet = filterSet, pageable = pageable)
    }

    /**
     * Creates a new employee in the system.
     * @param employeeRequest The employee to be created.
     * @return The created employee entity with generated ID.
     */
    fun create(employeeRequest: EmployeeRequest): Employee {
        verifyIntegrity(employeeId = null, employeeRequest = employeeRequest, reason = "Create Employee.")
        val employeeId = repository.create(employeeRequest = employeeRequest)
        return findById(employeeId = employeeId)!!
    }

    /**
     * Updates an employee's details in the system.
     * @param employeeId The ID of the employee to be updated.
     * @param employeeRequest The new details for the employee.
     * @return The updated employee entity if the update was successful, null otherwise.
     */
    fun update(employeeId: UUID, employeeRequest: EmployeeRequest): Employee? {
        verifyIntegrity(employeeId = employeeId, employeeRequest = employeeRequest, reason = "Update Employee.")
        val updatedCount = repository.update(employeeId = employeeId, employeeRequest = employeeRequest)
        return if (updatedCount > 0) findById(employeeId = employeeId) else null
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

    /**
     * Verifies if the employee's contact fields.
     *
     * @param employeeId The ID of the employee being verified.
     * @param employeeRequest The employee request details.
     * @param reason The reason for the email verification.
     * @throws BaseError If any of the fields is invalid.
     */
    private fun verifyIntegrity(employeeId: UUID?, employeeRequest: EmployeeRequest, reason: String) {
        employeeRequest.contact?.let { contact ->
            val phone: String = contact.phone
            if (!SecurityUtils.isValidPhone(phone = phone)) {
                EmployeeError.InvalidPhoneFormat(employeeId = employeeId, phone = phone).raise(reason = reason)
            }

            // Note: For sake of the example, we are already validating the email via the EmailString serializer
            // defined in the ContactRequest entity.
            // So, this validation is not really necessary and could be removed.
            // However, this verification shows how to raise a custom error for the email field
            // with a concrete error code and description.
            // The difference between this approach and the one used in the EmailString serializer,
            // is that the serializer is a generic one, and it is not aware of the context in which
            // it is being used, so it cannot provide a more concrete error code and description
            // as if using the following approach.
            val email: String = contact.email
            if (!SecurityUtils.isValidEmail(email = email)) {
                EmployeeError.InvalidEmailFormat(employeeId = employeeId, email = email).raise(reason = reason)
            }
        }
    }
}
