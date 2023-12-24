/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.Tracer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/**
 * Install the StatusPages feature for handling HTTP status codes.
 *
 * See: [Ktor Status Pages Documentation](https://ktor.io/docs/status-pages.html)
 */
fun Application.configureStatusPages() {
    val tracer = Tracer<Application>()

    install(StatusPages) {
        // Handle 401 Unauthorized status.
        status(HttpStatusCode.Unauthorized) { call: ApplicationCall, status: HttpStatusCode ->
            // Add WWW-Authenticate header to the response, indicating Basic Authentication is required.
            // This is specific to Basic Authentication, doesn't affect JWT.
            val realm = SettingsProvider.security.basicAuth.realm
            call.response.header(name = HttpHeaders.WWWAuthenticate, value = "Basic realm=\"${realm}\"")

            // Respond with 401 Unauthorized status code.
            call.respond(status = HttpStatusCode.Unauthorized, message = "$status")
        }

        // Additional exception handling.
        exception<IllegalArgumentException> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = formatErrorMessage(cause), throwable = cause)
            call.respond(status = HttpStatusCode.BadRequest, message = cause.localizedMessage)
        }
        exception<NotFoundException> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = formatErrorMessage(cause = cause), throwable = cause)
            call.respond(status = HttpStatusCode.NotFound, message = cause.localizedMessage)
        }
        exception<Throwable> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = formatErrorMessage(cause = cause), throwable = cause)
            call.respond(status = HttpStatusCode.InternalServerError, message = "Internal server error. ${cause.localizedMessage}")
        }
    }
}

private fun formatErrorMessage(cause: Throwable): String {
    return "\n----\nCaught ${cause.javaClass.simpleName} by Status Page plugin:"
}