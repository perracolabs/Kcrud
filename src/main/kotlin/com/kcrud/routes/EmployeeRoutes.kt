/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.controllers.EmployeeController
import com.kcrud.utils.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * Defines the employee-related routes for the Ktor application.
 */
class EmployeeRoutes(private val routingNode: Route) {

    fun routing() {
        routingNode.route("v1") {
            val controller by inject<EmployeeController>()

            // JWT Authentication for employee-related routes.
            if (SettingsProvider.get.jwt.isEnabled) {
                authenticate {
                    employeeRoutes(this, controller)
                    employeesRoutes(this, controller)
                }
            } else {
                // No authentication required.
                employeeRoutes(this, controller)
                employeesRoutes(this, controller)
            }
        }
    }

    private fun employeeRoutes(routeScope: Route, controller: EmployeeController) {
        routeScope {
            route("employee") {

                post { controller.create(call) }

                route("{id}") {
                    get { controller.get(call) }
                    put { controller.update(call) }
                    delete { controller.delete(call) }
                }
            }
        }
    }

    private fun employeesRoutes(routeScope: Route, controller: EmployeeController) {
        routeScope {
            route("employees") {
                get { controller.getAll(call) }
                delete { controller.deleteAll(call) }
            }
        }
    }
}

