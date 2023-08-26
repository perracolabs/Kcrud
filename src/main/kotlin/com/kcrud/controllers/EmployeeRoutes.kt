package com.kcrud.controllers

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.employee() {

    val controller: EmployeeController by inject()

    route("v1/employee/{id}") {
        get { controller.get(call) }

        put { controller.update(call) }

        patch { controller.patch(call) }

        delete { controller.delete(call) }
    }

    route("v1/employees") {
        get { controller.getAll(call) }

        delete { controller.deleteAll(call) }
    }

    route("v1/employee") {
        post { controller.create(call) }
    }
}
