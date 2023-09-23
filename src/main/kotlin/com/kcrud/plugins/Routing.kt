/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

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

    routing {
        root()

        // JWT Authentication for employee-related routes.
        if (SettingsProvider.get.jwt.isEnabled) {
            authenticate {
                employee()
            }
        } else {
            // No authentication required.
            employee()
        }
    }
}

private fun Route.root() {
    val appSettings = SettingsProvider.get

    if (appSettings.basicAuth.isEnabled) {
        // Basic Authentication for the root endpoint.
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
}

