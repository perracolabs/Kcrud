package com.kcrud.app.plugins

import com.kcrud.app.AppSettings
import com.kcrud.controllers.employee
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


/**
 * Initializes and sets up routing for the application.
 *
 * This includes a basic GET route for the root URL and conditional
 * JWT authentication for employee-related routes.
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html).
 */
fun Application.configureRouting() {

    val settings = AppSettings(config = environment.config)

    routing {

        // GET request at the root URL returns "Hello World!".
        get("/") {
            call.respondText("Hello World!")
        }

        // Conditionally enable JWT authentication for employee-related routes.
        if (settings.jwt.isEnabled) {
            authenticate {
                employee()
            }
        } else {
            // No JWT authentication required.
            employee()
        }
    }
}
