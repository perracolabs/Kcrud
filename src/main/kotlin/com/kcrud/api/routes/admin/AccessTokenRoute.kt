/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.routes.admin

import com.kcrud.config.env.security.authentication.AuthenticationToken
import com.kcrud.config.settings.AppSettings
import com.kcrud.plugins.RateLimitScope
import com.kcrud.utils.NetworkUtils
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Access-token endpoints.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
fun Route.accessTokenRoute() {

    val keyToken = "token"

    /**
     * Generates a new JWT token and sends it as a response.
     * If token generation fails, responds with an Internal Server Error status.
     */
    suspend fun ApplicationCall.respondWithToken() {
        try {
            val jwtToken = AuthenticationToken.generate()
            respond(hashMapOf(keyToken to jwtToken))
        } catch (e: Exception) {
            respond(HttpStatusCode.InternalServerError, "Failed to generate token.")
        }
    }

    route("auth/token") {

        // Endpoint for initial token generation; requires Basic Authentication credentials.
        rateLimit(RateLimitName(name = RateLimitScope.NEW_AUTH_TOKEN.key)) {
            authenticate(AppSettings.security.basicAuth.providerName) {
                // Creates a new token and responds with it.
                post("create") {
                    call.respondWithToken()
                }
            }
        }

        // Endpoint for token refresh.
        // No Basic Authentication is required here, but an existing token's validity will be checked.
        // For example, in Postman set the endpoint and in the Headers add an Authorization key
        // with a 'Bearer' holding a previous valid token.
        post("refresh") {
            val tokenState = AuthenticationToken.getState(call)

            when (tokenState) {
                AuthenticationToken.TokenState.Valid -> {
                    // Token is still valid; return the same token to the client.
                    val jwtToken = AuthenticationToken.fromHeader(call)
                    call.respond(hashMapOf(keyToken to jwtToken))
                }

                AuthenticationToken.TokenState.Expired -> {
                    // Token has expired; generate a new token and respond with it.
                    call.respondWithToken()
                }

                AuthenticationToken.TokenState.Invalid -> {
                    // Token is invalid; respond with an Unauthorized status.
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token.")
                }
            }
        }

        // Alternative example showing how to create tokens via the browser URL.
        // The browser URL requires a GET instead of a POST.
        authenticate(AppSettings.security.basicAuth.providerName) {
            // Generate auth tokens via the browser URL.
            get("form-create") {
                call.respondWithToken()
            }
        }
    }

    NetworkUtils.logEndpoints(
        reason = "Authentication Tokens. Requires an authorization header",
        endpoints = listOf("auth/token/create", "auth/token/refresh")
    )

    NetworkUtils.logEndpoints(
        reason = "New Authentication Tokens via the browser URL",
        endpoints = listOf("auth/token/form-create")
    )
}
