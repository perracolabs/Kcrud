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

    fun configure() {
        routingNode.route(API_VERSION) {
            if (SettingsProvider.get.jwt.isEnabled) {
                authenticate {
                    setupRoutes(this)
                }
            } else {
                setupRoutes(this)
            }
        }
    }

    private fun setupRoutes(routeScope: Route) {
        routeScope {
            val controller by inject<EmployeeController>()

            setupEmployeeRoutes(this, controller)
            setupEmployeesRoutes(this, controller)
        }
    }

    private fun setupEmployeeRoutes(routeScope: Route, controller: EmployeeController) {
        routeScope.route(EMPLOYEE_ROUTE) {

            post { controller.create(call) }

            route("{id}") {
                get { controller.get(call) }
                put { controller.update(call) }
                delete { controller.delete(call) }
            }
        }
    }

    private fun setupEmployeesRoutes(routeScope: Route, controller: EmployeeController) {
        routeScope.route(EMPLOYEES_ROUTE) {
            get { controller.getAll(call) }
            delete { controller.deleteAll(call) }
        }
    }

    companion object {
        private const val API_VERSION = "v1"
        private const val EMPLOYEE_ROUTE = "employee"
        private const val EMPLOYEES_ROUTE = "employees"
    }
}

