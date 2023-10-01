/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.controllers.EmploymentController
import com.kcrud.utils.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * Employment related endpoint configurations.
 */
class EmploymentRouting(private val routingNode: Route) {

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
            val controller by inject<EmploymentController>()

            // Base route for "employee".
            node.route(EmployeeRouting.EMPLOYEE_ROUTE) {

                // Nested route to specify an employee by ID.
                route("{${EmployeeRouting.EMPLOYEE_PATH_PARAMETER}}") {

                    // Nested route for Employments related to a specific employee.
                    route(EMPLOYMENTS_ROUTE) {

                        // For operations related to all employments associated with an employee.

                        post { controller.create(call) }
                        get { controller.getByEmployeeId(call) }

                        // For operations related to a single employment.

                        route("{$EMPLOYMENT_PATH_PARAMETER}") {
                            get { controller.get(call) }
                            put { controller.update(call) }
                            delete { controller.delete(call) }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val API_VERSION = "v1"
        private const val EMPLOYMENTS_ROUTE = "employments"
        const val EMPLOYMENT_PATH_PARAMETER = "employment_id"
    }
}

