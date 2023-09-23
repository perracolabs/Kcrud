/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.controllers.EmployeeController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

class EmployeeRoutes {
    /**
     * Defines the employee-related routes for the Ktor application.
     */
    fun employee(route: Route) {
        route {
            val controller by inject<EmployeeController>()

            route("v1") {

                // Routes for operations related to a single employee.
                route("employee") {

                    post { controller.create(call) }

                    route("{id}") {
                        get { controller.get(call) }
                        put { controller.update(call) }
                        delete { controller.delete(call) }
                    }
                }

                // Routes for operations related to multiple employees.
                route("employees") {
                    get { controller.getAll(call) }
                    delete { controller.deleteAll(call) }
                }
            }
        }
    }
}

