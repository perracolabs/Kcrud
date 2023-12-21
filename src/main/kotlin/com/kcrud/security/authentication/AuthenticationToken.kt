/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.kcrud.settings.SettingsProvider
import com.kcrud.utils.Tracer
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Security class responsible for verifying JWT tokens.
 *
 * This class contains the logic for verifying JWT tokens using HMAC256 algorithm.
 * If the token is invalid or any exception occurs, an UnauthorizedException will be thrown.
 */
internal object AuthenticationToken {
    private val tracer = Tracer.create<AuthenticationToken>()

    enum class TokenState {
        Valid, Expired, Invalid
    }

    /**
     * Verify the authorization token from the headers.
     * If invalid then the Unauthorized response is sent.
     */
    fun verify(call: ApplicationCall) {

        if (SettingsProvider.security.jwt.isEnabled) {
            val tokenState = getState(call)
            when (tokenState) {
                TokenState.Valid -> {
                    // Token is valid; continue processing.
                }

                TokenState.Expired -> {
                    runBlocking {
                        call.respond(HttpStatusCode.Unauthorized, "Unauthorized: Token has expired.")
                    }
                }

                TokenState.Invalid -> {
                    runBlocking {
                        call.respond(HttpStatusCode.Unauthorized, "Unauthorized: Token is invalid.")
                    }
                }
            }
        }
    }

    /**
     * Returns the current [TokenState] from the header authorization token.
     */
    fun getState(call: ApplicationCall): TokenState {

        return try {
            val token = fromHeader(call)
            val algorithm: Algorithm = Algorithm.HMAC256(SettingsProvider.security.jwt.secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            verifier.verify(JWT.decode(token))
            TokenState.Valid
        } catch (e: TokenExpiredException) {
            TokenState.Expired
        } catch (e: Exception) {
            tracer.error("Token verification failed: ${e.message}")
            TokenState.Invalid
        }
    }

    /**
     * Returns the current authorization token from the headers.
     */
    fun fromHeader(call: ApplicationCall): String {
        val authHeader = call.request.headers.entries().find {
            it.key.equals(HttpHeaders.Authorization, ignoreCase = true)
        }?.value?.get(0) ?: ""

        if (authHeader.isBlank() || !authHeader.startsWith(AuthScheme.Bearer, ignoreCase = true)) {
            throw IllegalArgumentException("Invalid Authorization header format.")
        }

        return authHeader.substring(AuthScheme.Bearer.length).trim()
    }

    /**
     * Generate a new authorization token.
     */
    fun generate(): String {
        val jwtSettings = SettingsProvider.security.jwt
        val tokenLifetimeMs = jwtSettings.tokenLifetime
        val expirationDate = Date(System.currentTimeMillis() + tokenLifetimeMs)

        tracer.debug("Generating new authorization token. Expiration: $expirationDate.")

        return JWT.create()
            .withAudience(jwtSettings.audience)
            .withIssuer(jwtSettings.issuer)
            .withExpiresAt(expirationDate)
            .sign(Algorithm.HMAC256(jwtSettings.secretKey))
    }
}
