package com.kcrud.controllers

import com.kcrud.data.models.EmployeeEntity
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
        val employeeId = call.parameters["id"]?.toIntOrNull()

        if (employeeId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
            return
        }

        val employee = service.findById(employeeId)

        if (employee == null) {
            call.respond(HttpStatusCode.NotFound, "Employee ID not found: $employeeId")
        } else {
            call.respond(employee)
        }
    }

    /**
     * Handles GET request to retrieve details of all employees.
     * Responds with a list of all employees.
     */
    suspend fun getAll(call: ApplicationCall) {
        val employees = service.findAll()
        call.respond(employees)
    }

    /**
     * Handles POST request to create a new employee.
     * Reads the employee details from the request, creates a new employee, and responds with the created employee details.
     */
    suspend fun create(call: ApplicationCall) {
        val employee = call.receive<EmployeeEntity>()
        val newEmployee = service.create(employee)
        call.respond(HttpStatusCode.Created, newEmployee)
    }

    /**
     * Handles PUT request to update details of a specific employee.
     * Reads the updated employee details from the request, updates the employee, and responds with the updated employee details.
     */
    suspend fun update(call: ApplicationCall) {
        val employeeId = call.parameters["id"]?.toIntOrNull()

        if (employeeId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
            return
        }

        val employeeData = call.receive<EmployeeEntity>()
        val updatedEmployee = service.update(employeeId, employeeData)

        if (updatedEmployee == null) {
            call.respond(HttpStatusCode.NotFound, "Employee ID not found: $employeeId")
        } else {
            call.respond(HttpStatusCode.OK, updatedEmployee)
        }
    }

    /**
     * Handles PATCH request to update details of a specific employee.
     * Reads the updated employee details from the request, updates the employee, and responds with the updated employee details.
     */
    suspend fun patch(call: ApplicationCall) {
        val employeeId = call.parameters["id"]?.toIntOrNull()

        if (employeeId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
            return
        }

        val employeePatch = call.receive<EmployeePatchDTO>()
        val patchedEmployee = service.patch(employeeId, employeePatch)

        if (patchedEmployee == null) {
            call.respond(HttpStatusCode.NotFound, "Employee ID not found: $employeeId")
        } else {
            call.respond(HttpStatusCode.OK, patchedEmployee)
        }
    }

    /**
     * Handles DELETE request to delete a specific employee.
     * Deletes the employee if found and responds with appropriate status code.
     */
    suspend fun delete(call: ApplicationCall) {
        val employeeId = call.parameters["id"]?.toIntOrNull()

        if (employeeId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid employee ID.")
            return
        }

        service.delete(employeeId)
        call.respond(HttpStatusCode.NoContent)
    }

    /**
     * Handles DELETE request to delete all employees.
     * Deletes all employees from the database and responds with an appropriate status code.
     */
    suspend fun deleteAll(call: ApplicationCall) {
        service.deleteAll()
        call.respond(HttpStatusCode.NoContent)
    }
}
