package com.kcrud.plugins

import com.kcrud.utils.appSettings
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


/**
 * Install the StatusPages feature for handling HTTP status codes.
 *
 * See: [Ktor Status Pages Documentation](https://ktor.io/docs/status-pages.html)
 */
fun Application.configureStatusPages() {

    val settings = appSettings()

    install(StatusPages) {
        // Handle 401 Unauthorized status.
        status(HttpStatusCode.Unauthorized) { call: ApplicationCall, status: HttpStatusCode ->
            // Add WWW-Authenticate header to the response, indicating Basic Authentication is required.
            // This is specific to Basic Authentication, doesn't affect JWT.
            call.response.header(name = "WWW-Authenticate", value = "Basic realm=\"${settings.basicAuth.realm}\"")

            // Respond with 401 Unauthorized status code.
            call.respond(status = HttpStatusCode.Unauthorized, message = "$status")
        }
    }
}
