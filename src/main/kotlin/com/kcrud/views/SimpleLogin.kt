/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.views

import com.kcrud.utils.SettingsProvider
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.html.*

/**
 * Handles generating the login form and managing form submission responses.
 *
 * See: [HTML DSL](https://ktor.io/docs/html-dsl.html)
 */
class SimpleLogin {

    /**
     * Generates and displays the login form when a GET request is made to the root path.
     */
    suspend fun generateForm(call: ApplicationCall) {

        call.respondHtml {
            body {
                h1 { +"Simple Login Page" }

                form(action = LOGIN_PATH, method = FormMethod.post) {
                    p {
                        label {
                            +"Username: "
                            textInput(name = KEY_USERNAME) {
                                placeholder = "Enter username"
                            }
                        }
                    }
                    p {
                        label {
                            +"Password: "
                            passwordInput(name = KEY_PASSWORD) {
                                placeholder = "Enter password"
                            }
                        }
                    }
                    p {
                        submitInput { value = "Login" }
                    }
                }
            }
        }
    }

    /**
     * Handles the form submission for authentication when a POST request is made.
     */
    suspend fun manageResponse(call: ApplicationCall) {
        val appSettings = SettingsProvider.get
        val parameters = call.receiveParameters()
        val username = parameters[KEY_USERNAME]
        val password = parameters[KEY_PASSWORD]

        if (username == appSettings.basicAuth.username && password == appSettings.basicAuth.password) {
            call.respondText("Authentication successful")
        } else {
            call.respondText("Authentication failed")
        }
    }

    companion object {
        const val LOGIN_PATH = "/login"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
    }
}