/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.data.models.employee.EmployeeParams
import com.kcrud.services.EmployeeService
import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.toUUIDOrNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


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
            val createdEmployee = service.create(employeeParams)
            call.respond(HttpStatusCode.Created, createdEmployee)
        }

        // Delete All
        delete {
            service.deleteAll().also {
                call.respond(HttpStatusCode.NoContent)
            }
        }

        route(RouteSegment.Employee.EMPLOYEE_ID_PATH) {

            // Find by employee ID
            get {
                val employeeId = call.parameters[RouteSegment.Employee.EMPLOYEE_ID]?.toUUIDOrNull()
                val employee = employeeId?.let { service.findById(it) }

                if (employee != null) {
                    call.respond(employee)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Employee not found.")
                }
            }

            // Update by employee ID
            put {
                val employeeId = call.parameters[RouteSegment.Employee.EMPLOYEE_ID]?.toUUIDOrNull()
                val employeeParams = call.receive<EmployeeParams>()
                val updatedEmployee = employeeId?.let { service.update(it, employeeParams) }

                if (updatedEmployee != null) {
                    call.respond(HttpStatusCode.OK, updatedEmployee)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Employee not found.")
                }
            }

            // Delete by employee ID
            delete {
                val employeeId = call.parameters[RouteSegment.Employee.EMPLOYEE_ID]?.toUUIDOrNull()
                employeeId?.let { service.delete(it) }
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    route(RouteSegment.API_VERSION) {
        route(RouteSegment.Employee.ROUTE) {

            if (SettingsProvider.security.jwt.isEnabled) {
                authenticate {
                    routeSetup()
                }
            } else {
                routeSetup()
            }
        }
    }
}
