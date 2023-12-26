/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.routes.main

import com.kcrud.api.views.SimpleLogin
import com.kcrud.config.settings.AppSettings
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Root endpoints.
 */
fun Route.rootRoute() {
    if (AppSettings.security.basicAuth.isEnabled) {
        // Basic Authentication for the root endpoint.
        if (AppSettings.security.basicAuth.loginForm) {

            // Use the login form to handle authentication.
            get("/") { SimpleLogin().generateForm(call) }
            post(SimpleLogin.LOGIN_PATH) { SimpleLogin().manageResponse(call) }

        } else {
            // Use built-in browser-based basic authentication.
            authenticate(AppSettings.security.basicAuth.providerName) {
                get("/") { call.respondText("Authentication successful") }
            }
        }
    } else {
        // No authentication required.
        get("/") { call.respondText("Hello World!") }
    }
}