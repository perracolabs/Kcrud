/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kcrud.base.admin.settings.AppSettings
import kcrud.base.exceptions.shared.KcrudException
import kcrud.base.utils.Tracer
import kotlinx.serialization.json.Json

/**
 * Install the StatusPages feature for handling HTTP status codes.
 *
 * See: [Ktor Status Pages Documentation](https://ktor.io/docs/status-pages.html)
 */
fun Application.configureStatusPages() {
    val tracer = Tracer<Application>()

    install(StatusPages) {

        // Custom application exceptions.
        exception<KcrudException> { call: ApplicationCall, cause ->
            tracer.error(message = cause.messageDetail(), throwable = cause)
            call.respondError(cause = cause)
        }

        // Handle 401 Unauthorized status.
        status(HttpStatusCode.Unauthorized) { call: ApplicationCall, status: HttpStatusCode ->
            // Add WWW-Authenticate header to the response, indicating Basic Authentication is required.
            // This is specific to Basic Authentication, doesn't affect JWT.
            val realm = AppSettings.security.basic.realm
            call.response.header(name = HttpHeaders.WWWAuthenticate, value = "Basic realm=\"${realm}\"")

            // Respond with 401 Unauthorized status code.
            call.respond(status = HttpStatusCode.Unauthorized, message = "$status")
        }

        // Security exception handling.
        status(HttpStatusCode.MethodNotAllowed) { call: ApplicationCall, status: HttpStatusCode ->
            call.respond(status = HttpStatusCode.MethodNotAllowed, message = "$status")
        }

        // Additional exception handling.
        exception<IllegalArgumentException> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = cause.message, throwable = cause)
            call.respond(status = HttpStatusCode.BadRequest, message = cause.localizedMessage)
        }
        exception<NotFoundException> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = cause.message, throwable = cause)
            call.respond(status = HttpStatusCode.NotFound, message = cause.localizedMessage)
        }
        exception<Throwable> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = cause.message, throwable = cause)
            val message = cause.cause?.localizedMessage ?: cause.localizedMessage
            call.respond(status = HttpStatusCode.InternalServerError, message = message)
        }
    }
}

private suspend fun ApplicationCall.respondError(cause: KcrudException) {
    // Set the ETag header with the error code.
    this.response.header(HttpHeaders.ETag, cause.error.code)

    // Serialize the error response.
    val json = Json.encodeToString(serializer = KcrudException.ErrorResponse.serializer(), value = cause.toErrorResponse())

    // Send the serialized error response.
    this.respondText(text = json, contentType = ContentType.Application.Json, status = cause.error.status)
}