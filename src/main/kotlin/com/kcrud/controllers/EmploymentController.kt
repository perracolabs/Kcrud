/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.controllers

import com.kcrud.data.models.Employment
import com.kcrud.routes.EmployeeRouting
import com.kcrud.routes.EmploymentRouting
import com.kcrud.services.EmploymentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.koin.core.component.KoinComponent

class EmploymentController(private val service: EmploymentService) : KoinComponent {

    /**
     * Handles GET request to retrieve details of a specific employment.
     *
     * Responds with the employment details if found, otherwise appropriate error messages.
     */
    suspend fun get(call: ApplicationCall) {
        call.parameters[EmploymentRouting.EMPLOYMENT_PATH_PARAMETER]?.toIntOrNull()?.let { employmentId ->
            service.findById(employmentId)?.also { employment ->
                call.respond(employment)
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Employment ID not found: $employmentId")
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employment ID argument.")
    }

    /**
     * Handles GET request to retrieve details of all employments for a given employee.
     *
     * Responds with a list of all employments.
     */
    suspend fun getByEmployeeId(call: ApplicationCall) {
        call.parameters[EmployeeRouting.EMPLOYEE_PATH_PARAMETER]?.toIntOrNull()?.let { employeeId ->
            call.respond(service.findByEmployeeId(employeeId))
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employee ID argument.")
    }

    /**
     * Handles POST request to create a new employment.
     *
     * Reads the employment details from the request, creates a new employment, and responds with the created employment details.
     */
    suspend fun create(call: ApplicationCall) {
        call.parameters[EmployeeRouting.EMPLOYEE_PATH_PARAMETER]?.toIntOrNull()?.let { employeeId ->
            call.receive<Employment>()
                .run { service.create(employeeId = employeeId, employment = this) }
                .also { newEmployment -> call.respond(status = HttpStatusCode.Created, message = newEmployment) }
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employee ID argument.")
    }

    /**
     * Handles PUT request to update details of a specific employment.
     *
     * Reads the updated employment details from the request, updates the employment, and responds with the updated employment details.
     */
    suspend fun update(call: ApplicationCall) {
        call.parameters[EmployeeRouting.EMPLOYEE_PATH_PARAMETER]?.toIntOrNull()?.let { employeeId ->
            call.parameters[EmploymentRouting.EMPLOYMENT_PATH_PARAMETER]?.toIntOrNull()?.let { employmentId ->

                val updatedEmployment = call.receive<Employment>().run {
                    service.update(employeeId = employeeId, employmentId = employmentId, employment = this)
                }

                updatedEmployment?.also { employment ->
                    call.respond(status = HttpStatusCode.OK, message = employment)
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "Employment ID not found: $employmentId")

            } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employment ID argument.")
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employee ID argument.")
    }

    /**
     * Handles DELETE request to delete a specific employment.
     *
     * Deletes the employment if found and responds with appropriate status code.
     */
    suspend fun delete(call: ApplicationCall) {
        call.parameters[EmploymentRouting.EMPLOYMENT_PATH_PARAMETER]?.toIntOrNull()?.let { employmentId ->
            service.delete(employmentId)
            call.respond(HttpStatusCode.NoContent)
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employment ID.")
    }
}
