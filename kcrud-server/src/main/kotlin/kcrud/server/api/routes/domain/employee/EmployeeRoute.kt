/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.api.routes.domain.employee

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcrud.base.admin.settings.AppSettings
import kcrud.base.data.pagination.getPageable
import kcrud.base.data.utils.toUUIDOrNull
import kcrud.server.api.routes.domain.employment.employmentRoute
import kcrud.server.domain.entities.employee.EmployeeFilterSet
import kcrud.server.domain.entities.employee.EmployeeRequest
import kcrud.server.domain.exceptions.EmployeeError
import kcrud.server.domain.services.EmployeeService
import org.koin.ktor.ext.inject
import java.util.*

/**
 * Employee related endpoints.
 *
 * These endpoints are defined in a single function
 * to demonstrate how to encapsulate routes in a single function.
 *
 * See [Application Structure](https://ktor.io/docs/structuring-applications.html) for examples
 * of how to organize routes in diverse ways.
 *
 * See [Route.employmentRoute] for an example of how to define multiple routes in multiple functions.
 */
fun Route.employeeRoute() {

    val routeSetup: Route.() -> Unit = {

        val service by inject<EmployeeService>()

        // Find All
        get {
            val pageable = call.getPageable()
            val employees = service.findAll(pageable = pageable)
            call.respond(message = employees)
        }

        // Create
        post {
            val employeeRequest = call.receive<EmployeeRequest>()
            val createdEmployee = service.create(employeeRequest = employeeRequest)
            call.respond(status = HttpStatusCode.Created, message = createdEmployee)
        }

        // Delete All
        delete {
            service.deleteAll().also { deletedCount ->
                call.respond(status = HttpStatusCode.OK, message = deletedCount)
            }
        }

        // Search (Filter)
        post("/search") {
            val pageable = call.getPageable()
            val filterSet = call.receive<EmployeeFilterSet>()
            val employees = service.filter(filterSet = filterSet, pageable = pageable)
            call.respond(message = employees)
        }

        route("{employee_id}") {

            // Find by employee ID
            get {
                val employeeId = call.getEmployeeId()
                val employee = service.findById(employeeId = employeeId)

                employee?.let {
                    call.respond(message = employee)
                } ?: EmployeeError.EmployeeNotFound(employeeId = employeeId).raise()
            }

            // Update by employee ID
            put {
                val employeeId = call.getEmployeeId()
                val employeeRequest = call.receive<EmployeeRequest>()
                val updatedEmployee = service.update(employeeId = employeeId, employeeRequest = employeeRequest)

                updatedEmployee?.let {
                    call.respond(status = HttpStatusCode.OK, message = updatedEmployee)
                } ?: EmployeeError.EmployeeNotFound(employeeId = employeeId).raise()
            }

            // Delete by employee ID
            delete {
                val employeeId = call.getEmployeeId()
                service.delete(employeeId = employeeId).also { deletedCount ->
                    call.respond(status = HttpStatusCode.OK, message = deletedCount)
                }
            }
        }
    }

    route("${AppSettings.deployment.apiVersion}/employees") {
        routeSetup()
    }
}

private fun ApplicationCall.getEmployeeId(): UUID {
    return parameters["employee_id"]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employee ID argument.")
}
