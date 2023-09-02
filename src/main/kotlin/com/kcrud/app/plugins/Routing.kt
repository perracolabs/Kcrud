package com.kcrud.app.plugins

import com.kcrud.app.appSettings
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

    val settings = appSettings()

    routing {
        // Basic Authentication for the root endpoint.
        if (settings.basicAuth.isEnabled) {
            authenticate(settings.basicAuth.providerName) {
                get("/") {
                    call.respondText("Hello World! You are authenticated.")
                }
            }
        } else {
            // No authentication required.
            get("/") {
                call.respondText("Hello World!")
            }
        }

        // JWT Authentication for employee-related routes.
        if (settings.jwt.isEnabled) {
            authenticate {
                employee()
            }
        } else {
            // No authentication required.
            employee()
        }
    }
}
