package com.kcrud.controllers

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


/**
 * Defines the employee-related routes for the Ktor application.
 */
fun Route.employee() {

    val controller: EmployeeController by inject()

    route("v1") {

        // Routes for operations related to a single employee.
        route("employee") {

            route("{id}") {
                get { controller.get(call) }
                put { controller.update(call) }
                patch { controller.patch(call) }
                delete { controller.delete(call) }
            }

            // Route for creating a new employee.
            post { controller.create(call) }
        }

        // Routes for operations related to multiple employees.
        route("employees") {
            get { controller.getAll(call) }
            delete { controller.deleteAll(call) }
        }
    }
}


