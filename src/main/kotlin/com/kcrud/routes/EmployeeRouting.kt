/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.data.models.Employee
import com.kcrud.services.EmployeeService
import com.kcrud.utils.SettingsProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * Employee related endpoint configurations.
 */
class EmployeeRouting(private val routingNode: Route) {

    fun configure() {
        routingNode.route(API_VERSION) {
            if (SettingsProvider.get.jwt.isEnabled) {
                authenticate {
                    setupRoutes(node = this)
                }
            } else {
                setupRoutes(node = this)
            }
        }
    }

    private fun setupRoutes(node: Route) {
        node.apply {
            val service by inject<EmployeeService>()

            route(EMPLOYEE_ROUTE) {
                findAll(node = this, service = service)
                create(node = this, service = service)
                deleteAll(node = this, service = service)

                route("{$EMPLOYEE_PATH_PARAMETER}") {
                    findById(node = this, service = service)
                    update(node = this, service = service)
                    delete(node = this, service = service)
                }
            }
        }
    }

    private fun findAll(node: Route, service: EmployeeService) {
        node.apply {
            get {
                val employees = service.findAll()
                call.respond(employees)
            }
        }
    }

    private fun create(node: Route, service: EmployeeService) {
        node.apply {
            post {
                val newEmployee = call.receive<Employee>()
                val createdEmployee = service.create(newEmployee)
                call.respond(HttpStatusCode.Created, createdEmployee)
            }
        }
    }

    private fun deleteAll(node: Route, service: EmployeeService) {
        node.apply {
            delete {
                service.deleteAll().also {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }

    private fun findById(node: Route, service: EmployeeService) {
        node.apply {
            get {
                val employeeId = call.parameters[EMPLOYEE_PATH_PARAMETER]?.toIntOrNull()
                val employee = employeeId?.let { service.findById(it) }

                if (employee != null) {
                    call.respond(employee)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Employee not found.")
                }
            }
        }
    }

    private fun update(node: Route, service: EmployeeService) {
        node.apply {
            put {
                val employeeId = call.parameters[EMPLOYEE_PATH_PARAMETER]?.toIntOrNull()
                val updatedInfo = call.receive<Employee>()
                val updatedEmployee = employeeId?.let { service.update(it, updatedInfo) }

                if (updatedEmployee != null) {
                    call.respond(HttpStatusCode.OK, updatedEmployee)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Employee not found.")
                }
            }
        }
    }

    private fun delete(node: Route, service: EmployeeService) {
        node.apply {
            delete {
                val employeeId = call.parameters[EMPLOYEE_PATH_PARAMETER]?.toIntOrNull()
                employeeId?.let { service.delete(it) }
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    companion object {
        const val API_VERSION = "v1"
        const val EMPLOYEE_ROUTE = "employees"
        const val EMPLOYEE_PATH_PARAMETER = "employee_id"
    }
}


