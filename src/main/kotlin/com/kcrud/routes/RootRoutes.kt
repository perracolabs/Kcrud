/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.routes

import com.kcrud.utils.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Defines the root endpoint for the Ktor application.
 */
class RootRoutes(private val routingNode: Route) {
    private val appSettings = SettingsProvider.get

    fun routing() {
        routingNode {
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
    }
}