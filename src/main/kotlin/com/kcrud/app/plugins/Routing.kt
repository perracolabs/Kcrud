package com.kcrud.app.plugins

import com.kcrud.controllers.employee
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


/**
 * Application extension function to configure the routing settings.
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 */
fun Application.configureRouting() {

    // Basic routing for the root URL path.
    routing {
        get("/") {
            // Respond with a text message when the root URL is accessed.
            call.respondText("Hello World! But, there is nothing to see here.")
        }
    }

    // Additional routing configuration specific to the 'employee' domain.
    routing {
        employee()
    }
}
