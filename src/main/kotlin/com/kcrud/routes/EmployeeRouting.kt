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
            val controller by inject<EmployeeController>()

            employeeRoutes(node = this, controller = controller)
            employeesRoutes(node = this, controller = controller)
        }
    }

    private fun employeeRoutes(node: Route, controller: EmployeeController) {
        node.route(EMPLOYEE_ROUTE) {

            post { controller.create(call) }

            route("{id}") {
                get { controller.get(call) }
                put { controller.update(call) }
                delete { controller.delete(call) }
            }
        }
    }

    private fun employeesRoutes(node: Route, controller: EmployeeController) {
        node.route(EMPLOYEES_ROUTE) {
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

