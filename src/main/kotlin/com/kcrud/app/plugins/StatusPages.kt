package com.kcrud.app.plugins

import com.kcrud.utils.UnauthorizedException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*


/**
 * Configures and installs the StatusPages feature for exception handling.
 *
 * This is specifically tailored to capture UnauthorizedException and return
 * an HTTP 401 Unauthorized status code, primarily to manage unauthorized access in GraphQL queries.
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UnauthorizedException> { context, error ->
            context.respond(HttpStatusCode.Unauthorized, error.message ?: "Unauthorized")
        }
    }
}
