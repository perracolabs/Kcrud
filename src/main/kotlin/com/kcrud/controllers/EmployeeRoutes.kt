package com.kcrud.controllers

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


/**
 * Defines the employee-related routes for the Ktor application.
 */
fun Route.employee() {

    val controller: EmployeeController by inject()

    // Define routes for operations related to a single employee.
    route("v1/employee/{id}") {
        get { controller.get(call) }
        put { controller.update(call) }
        patch { controller.patch(call) }
        delete { controller.delete(call) }
    }

    // Define routes for operations related to multiple employees.
    route("v1/employees") {
        get { controller.getAll(call) }
        delete { controller.deleteAll(call) }
    }

    // Define route for creating a new employee.
    route("v1/employee") {
        post { controller.create(call) }
    }
}
