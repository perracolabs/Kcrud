/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.data.models.employment.EmploymentParams
import com.kcrud.services.EmploymentService
import com.kcrud.utils.toUUIDOrNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*


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

    route(RouteSegment.API_VERSION) {
        route(RouteSegment.Employee.ROUTE) {
            route(RouteSegment.Employee.EMPLOYEE_ID_PATH) {
                route(RouteSegment.Employment.ROUTE) {
                    setupEmploymentRoutes(service)
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

    route(RouteSegment.Employment.EMPLOYMENT_ID_PATH) {
        findEmploymentById(service)
        updateEmploymentById(service)
        deleteEmploymentById(service)
    }
}

private fun Route.createEmployment(service: EmploymentService) {
    post {
        val employeeId = call.getEmployeeId()
        val employmentParams = call.receive<EmploymentParams>()
        val newEmployment = service.create(employeeId, employmentParams)
        call.respond(HttpStatusCode.Created, newEmployment)
    }
}

private fun Route.findEmploymentByEmployeeId(service: EmploymentService) {
    get {
        val employeeId = call.getEmployeeId()
        val employments = service.findByEmployeeId(employeeId)
        call.respond(employments)
    }
}

private fun Route.findEmploymentById(service: EmploymentService) {
    get {
        val employeeId = call.getEmployeeId()
        val employmentId = call.getEmploymentId()

        service.findById(employeeId = employeeId, employmentId = employmentId)?.also { employment ->
            call.respond(employment)
        } ?: call.respondNotFound(employeeId = employeeId, employmentId = employmentId)
    }
}

private fun Route.updateEmploymentById(service: EmploymentService) {
    put {
        val employeeId = call.getEmployeeId()
        val employmentId = call.getEmploymentId()

        val updatedEmployment = call.receive<EmploymentParams>().let { employmentParams ->
            service.update(employeeId = employeeId, employmentId = employmentId, employment = employmentParams)
        }

        updatedEmployment?.also { employment ->
            call.respond(status = HttpStatusCode.OK, message = employment)
        } ?: call.respondNotFound(employeeId = employeeId, employmentId = employmentId)
    }
}

private fun Route.deleteEmploymentByEmployeeId(service: EmploymentService) {
    delete {
        val employeeId = call.getEmployeeId()
        service.deleteAll(employeeId = employeeId).also { deletedCount ->
            call.respond(HttpStatusCode.OK, deletedCount)
        }
    }
}

private fun Route.deleteEmploymentById(service: EmploymentService) {
    delete {
        val employmentId = call.getEmploymentId()
        service.delete(employmentId = employmentId).also { deletedCount ->
            call.respond(HttpStatusCode.OK, deletedCount)
        }
    }
}

private fun ApplicationCall.getEmployeeId(): UUID {
    return parameters[RouteSegment.Employee.EMPLOYEE_ID]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employee ID argument.")
}

private fun ApplicationCall.getEmploymentId(): UUID {
    return parameters[RouteSegment.Employment.EMPLOYMENT_ID]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employment ID argument.")
}

private suspend fun ApplicationCall.respondNotFound(employeeId: UUID, employmentId: UUID) {
    respond(
        status = HttpStatusCode.NotFound,
        message = "Employment ID not found '$employmentId' for employee ID '$employeeId'."
    )
}
