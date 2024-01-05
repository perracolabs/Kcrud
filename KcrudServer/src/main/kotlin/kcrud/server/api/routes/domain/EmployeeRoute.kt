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
import kcrud.core.data.pagination.getPageable
import kcrud.core.data.utils.toUUIDOrNull
import kcrud.server.domain.entities.employee.EmployeeFilterSet
import kcrud.server.domain.entities.employee.EmployeeRequest
import kcrud.server.domain.services.EmployeeService
import org.koin.ktor.ext.inject
import java.util.*

/**
 * Employee related endpoints.
 *
 * These endpoints are defined in a single function
 * to demonstrate how to encapsulate routes in a single function.
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
            call.respond(employees)
        }

        // Create
        post {
            val employeeRequest = call.receive<EmployeeRequest>()
            val createdEmployee = service.create(employeeRequest = employeeRequest)
            call.respond(HttpStatusCode.Created, createdEmployee)
        }

        // Delete All
        delete {
            service.deleteAll().also { deletedCount ->
                call.respond(HttpStatusCode.OK, deletedCount)
            }
        }

        // Search (Filter)
        post("/search") {
            val pageable = call.getPageable()
            val filterSet = call.receive<EmployeeFilterSet>()
            val employees = service.filter(filterSet = filterSet, pageable = pageable)
            call.respond(employees)
        }

        route("{employee_id}") {

            // Find by employee ID
            get {
                val employeeId = call.getEmployeeId()
                val employee = service.findById(employeeId = employeeId)

                employee?.let {
                    call.respond(employee)
                } ?: call.respondNotFound(employeeId = employeeId)
            }

            // Update by employee ID
            put {
                val employeeId = call.getEmployeeId()
                val employeeRequest = call.receive<EmployeeRequest>()
                val updatedEmployee = service.update(employeeId = employeeId, employeeRequest = employeeRequest)

                updatedEmployee?.let {
                    call.respond(HttpStatusCode.OK, updatedEmployee)
                } ?: call.respondNotFound(employeeId = employeeId)
            }

            // Delete by employee ID
            delete {
                val employeeId = call.getEmployeeId()
                service.delete(employeeId = employeeId).also { deletedCount ->
                    call.respond(HttpStatusCode.OK, deletedCount)
                }
            }
        }
    }

    route("v1/employees") {
        routeSetup()
    }
}

private fun ApplicationCall.getEmployeeId(): UUID {
    return parameters["employee_id"]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employee ID argument.")
}

private suspend fun ApplicationCall.respondNotFound(employeeId: UUID) {
    respond(
        status = HttpStatusCode.NotFound,
        message = "Employee ID not found '$employeeId'."
    )
}
