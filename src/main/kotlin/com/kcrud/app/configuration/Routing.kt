package com.kcrud.app.configuration

import com.kcrud.controllers.employee
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World! But, there is nothing to see here.")
        }
    }

    routing {
        employee()
    }
}
