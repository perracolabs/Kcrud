/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.views

import com.kcrud.admin.env.security.authentication.BasicCredentials
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
internal object SimpleLogin {

    /** The path to the login form. */
    const val LOGIN_PATH = "/login"

    // The key for the username field.
    private const val KEY_USERNAME = "username"

    // The key for the password field.
    private const val KEY_PASSWORD = "password"

    /**
     * Generates and displays the login form when a GET request is made to the root path.
     */
    suspend fun showLoginForm(call: ApplicationCall) {
        call.respondHtml {
            buildLoginForm()
        }
    }

    /**
     * Handles the form submission for authentication when a POST request is made.
     */
    suspend fun manageResponse(call: ApplicationCall) {
        val parameters = call.receiveParameters()
        val username = parameters[KEY_USERNAME]
        val password = parameters[KEY_PASSWORD]

        val isValid = BasicCredentials.verify(username = username, password = password)

        if (isValid) {
            call.respondText("Authentication successful")
        } else {
            call.respondText("Authentication failed")
        }
    }

    /**
     * Generates the login form using HTML DSL.
     *
     * See: [HTML DSL](https://ktor.io/docs/html-dsl.html)
     */
    private fun HTML.buildLoginForm() {
        body {
            h1 { +"Kcrud Login" }

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
