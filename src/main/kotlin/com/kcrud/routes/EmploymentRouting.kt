/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.data.models.employment.EmploymentInput
import com.kcrud.services.EmploymentService
import com.kcrud.utils.SettingsProvider
import com.kcrud.utils.toUUIDOrNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


/**
 * Employment endpoints.
 *
 * These end points are defined in multiple functions
 * to demonstrate how to organize routes separately
 * and potentially in multiple files.
 *
 * See [Route.employeeRouting] for an example of how to define multiple routes in a single function.
 */
fun Route.employmentRouting() {
    val service by inject<EmploymentService>()

    route(RoutingParams.API_VERSION) {
        route(RoutingParams.EMPLOYEE_ROUTE) {
            route("{${RoutingParams.EMPLOYEE_PATH_PARAMETER}}") {
                route(RoutingParams.EMPLOYMENTS_ROUTE) {

                    if (SettingsProvider.get.jwt.isEnabled) {
                        authenticate {
                            setupEmploymentRoutes(service)
                        }
                    } else {
                        setupEmploymentRoutes(service)
                    }

                }
            }
        }
    }
}

/**
 * Separate function to configure employment routes.
 * These could also be defined in separate files for better organization
 */
private fun Route.setupEmploymentRoutes(service: EmploymentService) {
    createEmployment(service)
    findEmploymentByEmployeeId(service)
    deleteEmploymentByEmployeeId(service)

    route("{${RoutingParams.EMPLOYMENT_PATH_PARAMETER}}") {
        findEmploymentById(service)
        updateEmploymentById(service)
        deleteEmploymentById(service)
    }
}

private fun Route.createEmployment(service: EmploymentService) {
    post {
        val employeeId = call.parameters[RoutingParams.EMPLOYEE_PATH_PARAMETER]?.toUUIDOrNull()

        employeeId?.let {
            val employment = call.receive<EmploymentInput>()
            val newEmployment = service.create(employeeId, employment)
            call.respond(HttpStatusCode.Created, newEmployment)
        } ?: call.respond(HttpStatusCode.NotFound, "Employee not found.")
    }
}

private fun Route.findEmploymentByEmployeeId(service: EmploymentService) {
    get {
        val employeeId = call.parameters[RoutingParams.EMPLOYEE_PATH_PARAMETER]?.toUUIDOrNull()

        employeeId?.let {
            val employments = service.findByEmployeeId(employeeId)
            call.respond(employments)
        } ?: call.respond(HttpStatusCode.NotFound, "Employee not found.")
    }
}

private fun Route.findEmploymentById(service: EmploymentService) {
    get {
        call.parameters[RoutingParams.EMPLOYMENT_PATH_PARAMETER]?.toUUIDOrNull()?.let { employmentId ->
            service.findById(employmentId)?.also { employment ->
                call.respond(employment)
            } ?: call.respond(HttpStatusCode.NotFound, "Employment ID not found: $employmentId")
        } ?: call.respond(HttpStatusCode.BadRequest, "Invalid employment ID argument.")
    }
}

private fun Route.updateEmploymentById(service: EmploymentService) {
    put {
        call.parameters[RoutingParams.EMPLOYEE_PATH_PARAMETER]?.toUUIDOrNull()?.let { employeeId ->

            call.parameters[RoutingParams.EMPLOYMENT_PATH_PARAMETER]?.toUUIDOrNull()?.let { employmentId ->

                val updatedEmployment = call.receive<EmploymentInput>().run {
                    service.update(employeeId = employeeId, employmentId = employmentId, employment = this)
                }

                updatedEmployment?.also { employment ->
                    call.respond(status = HttpStatusCode.OK, message = employment)
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "Employment ID not found: $employmentId")

            } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employment ID argument.")
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employee ID argument.")
    }
}

private fun Route.deleteEmploymentByEmployeeId(service: EmploymentService) {
    delete {
        call.parameters[RoutingParams.EMPLOYEE_PATH_PARAMETER]?.toUUIDOrNull()?.let { employeeId ->
            service.deleteAll(employeeId)
            call.respond(HttpStatusCode.NoContent)
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employeeId ID.")
    }
}

private fun Route.deleteEmploymentById(service: EmploymentService) {
    delete {
        call.parameters[RoutingParams.EMPLOYMENT_PATH_PARAMETER]?.toUUIDOrNull()?.let { employmentId ->
            service.delete(employmentId)
            call.respond(HttpStatusCode.NoContent)
        } ?: call.respond(status = HttpStatusCode.BadRequest, message = "Invalid employment ID.")
    }
}
