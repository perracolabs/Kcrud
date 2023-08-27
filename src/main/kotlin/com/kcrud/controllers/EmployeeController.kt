package com.kcrud.controllers

import com.kcrud.data.models.EmployeeInput
import com.kcrud.data.models.EmployeePatchDTO
import com.kcrud.services.EmployeeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.component.KoinComponent


class EmployeeController(private val service: EmployeeService) : KoinComponent {

    /**
     * Handles GET request to retrieve details of a specific employee.
     * Responds with the employee details if found, otherwise appropriate error messages.
     */
    suspend fun get(call: ApplicationCall) {
        call.parameters["id"]?.toIntOrNull()?.let { employeeId ->
            service.findById(employeeId)?.also { employee ->
                call.respond(employee)
            } ?: call.respond(HttpStatusCode.NotFound, "Employee ID not found: $employeeId")
        } ?: call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
    }

    /**
     * Handles GET request to retrieve details of all employees.
     * Responds with a list of all employees.
     */
    suspend fun getAll(call: ApplicationCall) {
        call.respond(service.findAll())
    }

    /**
     * Handles POST request to create a new employee.
     * Reads the employee details from the request, creates a new employee, and responds with the created employee details.
     */
    suspend fun create(call: ApplicationCall) {
        call.receive<EmployeeInput>()
            .run { service.create(this) }
            .also { newEmployee -> call.respond(HttpStatusCode.Created, newEmployee) }
    }

    /**
     * Handles PUT request to update details of a specific employee.
     * Reads the updated employee details from the request, updates the employee, and responds with the updated employee details.
     */
    suspend fun update(call: ApplicationCall) {
        call.parameters["id"]?.toIntOrNull()?.let { employeeId ->
            val updatedEmployee = call.receive<EmployeeInput>().run {
                service.update(employeeId, this)
            }

            updatedEmployee?.also { employee ->
                call.respond(HttpStatusCode.OK, employee)
            } ?: call.respond(HttpStatusCode.NotFound, "Employee ID not found: $employeeId")

        } ?: call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
    }

    /**
     * Handles PATCH request to update details of a specific employee.
     * Reads the updated employee details from the request, updates the employee, and responds with the updated employee details.
     */
    suspend fun patch(call: ApplicationCall) {
        call.parameters["id"]?.toIntOrNull()?.let { employeeId ->
            val patchedEmployee = call.receive<EmployeePatchDTO>().run {
                service.patch(employeeId, this)
            }

            patchedEmployee?.also { employee ->
                call.respond(HttpStatusCode.OK, employee)
            } ?: call.respond(HttpStatusCode.NotFound, "Employee ID not found: $employeeId")
        } ?: call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
    }

    /**
     * Handles DELETE request to delete a specific employee.
     * Deletes the employee if found and responds with appropriate status code.
     */
    suspend fun delete(call: ApplicationCall) {
        call.parameters["id"]?.toIntOrNull()?.let { employeeId ->
            service.delete(employeeId)
            call.respond(HttpStatusCode.NoContent)
        } ?: call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
    }

    /**
     * Handles DELETE request to delete all employees.
     * Deletes all employees from the database and responds with an appropriate status code.
     */
    suspend fun deleteAll(call: ApplicationCall) {
        service.deleteAll().also { call.respond(HttpStatusCode.NoContent) }
    }
}
