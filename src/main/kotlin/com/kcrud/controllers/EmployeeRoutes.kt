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


