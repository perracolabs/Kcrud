/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.routes.domain

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcrud.base.admin.settings.AppSettings
import kcrud.base.data.utils.toUUIDOrNull
import kcrud.server.domain.entities.employment.EmploymentRequest
import kcrud.server.domain.exceptions.EmploymentError
import kcrud.server.domain.services.EmploymentService
import org.koin.ktor.ext.inject
import java.util.*

/**
 * Employment endpoints.
 *
 * These endpoints are defined in multiple functions
 * to demonstrate how to organize routes separately
 * and potentially in multiple files.
 *
 * See [Route.employeeRoute] for an example of how to define multiple routes in a single function.
 */
fun Route.employmentRoute() {
    val service by inject<EmploymentService>()

    route("${AppSettings.deployment.apiVersion}/employees/{employee_id}/employments") {
        setupEmploymentRoutes(service)
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

    route("{employment_id}") {
        findEmploymentById(service)
        updateEmploymentById(service)
        deleteEmploymentById(service)
    }
}

private fun Route.createEmployment(service: EmploymentService) {
    post {
        val employeeId = call.getEmployeeId()
        val employmentRequest = call.receive<EmploymentRequest>()
        val newEmployment = service.create(employeeId = employeeId, employmentRequest = employmentRequest)
        call.respond(status = HttpStatusCode.Created, message = newEmployment)
    }
}

private fun Route.findEmploymentByEmployeeId(service: EmploymentService) {
    get {
        val employeeId = call.getEmployeeId()
        val employments = service.findByEmployeeId(employeeId = employeeId)
        call.respond(message = employments)
    }
}

private fun Route.findEmploymentById(service: EmploymentService) {
    get {
        val employeeId = call.getEmployeeId()
        val employmentId = call.getEmploymentId()

        service.findById(employeeId = employeeId, employmentId = employmentId)?.also { employment ->
            call.respond(status = HttpStatusCode.OK, message = employment)
        } ?: EmploymentError.EmploymentNotFound(
            employeeId = employeeId,
            employmentId = employmentId
        ).raise()
    }
}

private fun Route.updateEmploymentById(service: EmploymentService) {
    put {
        val employeeId = call.getEmployeeId()
        val employmentId = call.getEmploymentId()

        val updatedEmployment = call.receive<EmploymentRequest>().let { employmentRequest ->
            service.update(employeeId = employeeId, employmentId = employmentId, employmentRequest = employmentRequest)
        }

        updatedEmployment?.also { employment ->
            call.respond(status = HttpStatusCode.OK, message = employment)
        } ?: EmploymentError.EmploymentNotFound(
            employeeId = employeeId,
            employmentId = employmentId
        ).raise()
    }
}

private fun Route.deleteEmploymentByEmployeeId(service: EmploymentService) {
    delete {
        val employeeId = call.getEmployeeId()
        service.deleteAll(employeeId = employeeId).also { deletedCount ->
            call.respond(status = HttpStatusCode.OK, message = deletedCount)
        }
    }
}

private fun Route.deleteEmploymentById(service: EmploymentService) {
    delete {
        val employmentId = call.getEmploymentId()
        service.delete(employmentId = employmentId).also { deletedCount ->
            call.respond(status = HttpStatusCode.OK, message = deletedCount)
        }
    }
}

private fun ApplicationCall.getEmployeeId(): UUID {
    return parameters["employee_id"]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employee ID argument.")
}

private fun ApplicationCall.getEmploymentId(): UUID {
    return parameters["employment_id"]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employment ID argument.")
}
