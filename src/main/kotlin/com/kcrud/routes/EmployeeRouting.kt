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

            route(EMPLOYEE_ROUTE) {

                // For operations related to all employees.

                get { controller.getAll(call) }
                delete { controller.deleteAll(call) }

                // For operations related to a single employee.

                post { controller.create(call) }

                route("{$EMPLOYEE_PATH_PARAMETER}") {
                    get { controller.get(call) }
                    put { controller.update(call) }
                    delete { controller.delete(call) }
                }
            }
        }
    }

    companion object {
        private const val API_VERSION = "v1"
        const val EMPLOYEE_ROUTE = "employees"
        const val EMPLOYEE_PATH_PARAMETER = "employee_id"
    }
}

