package com.kcrud.plugins

import com.kcrud.controllers.employee
import com.kcrud.utils.SettingsProvider
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
 * See: [Ktor Routing Documentation](https://ktor.io/docs/routing-in-ktor.html)
 */
fun Application.configureRouting() {

    val appSettings = SettingsProvider.get

    routing {
        // Basic Authentication for the root endpoint.
        if (appSettings.basicAuth.isEnabled) {
            authenticate(appSettings.basicAuth.providerName) {
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
        if (appSettings.jwt.isEnabled) {
            authenticate {
                employee()
            }
        } else {
            // No authentication required.
            employee()
        }
    }
}
