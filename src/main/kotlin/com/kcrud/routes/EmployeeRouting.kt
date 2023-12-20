/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.data.models.employee.EmployeeParams
import com.kcrud.services.EmployeeService
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
 * Employee related endpoints.
 *
 * These end points are defined in a single function
 * to demonstrate how to encapsulate routes in a single function.
 *
 * See [Route.employmentRouting] for an example of how to define multiple routes in multiple functions.
 */
fun Route.employeeRouting() {

    val routeSetup: Route.() -> Unit = {

        val service by inject<EmployeeService>()

        // Find All
        get {
            val employees = service.findAll()
            call.respond(employees)
        }

        // Create
        post {
            val employeeParams = call.receive<EmployeeParams>()
            val createdEmployee = service.create(employee = employeeParams)
            call.respond(HttpStatusCode.Created, createdEmployee)
        }

        // Delete All
        delete {
            service.deleteAll().also { deletedCount ->
                call.respond(HttpStatusCode.OK, deletedCount)
            }
        }

        route(RouteSegment.Employee.EMPLOYEE_ID_PATH) {

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
                val employeeParams = call.receive<EmployeeParams>()
                val updatedEmployee = service.update(employeeId = employeeId, employee = employeeParams)

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

    route(RouteSegment.API_VERSION) {
        route(RouteSegment.Employee.ROUTE) {
            routeSetup()
        }
    }
}

private fun ApplicationCall.getEmployeeId(): UUID {
    return parameters[RouteSegment.Employee.EMPLOYEE_ID]?.toUUIDOrNull()
        ?: throw BadRequestException("Invalid employee ID argument.")
}

private suspend fun ApplicationCall.respondNotFound(employeeId: UUID) {
    respond(
        status = HttpStatusCode.NotFound,
        message = "Employee ID not found '$employeeId'."
    )
}
