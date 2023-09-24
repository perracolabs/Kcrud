/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.kcrud.utils.SettingsProvider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Security class responsible for verifying JWT tokens.
 *
 * This class contains the logic for verifying JWT tokens using HMAC256 algorithm.
 * If the token is invalid or any exception occurs, an UnauthorizedException will be thrown.
 */
object AuthenticationToken {
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    // Hardcoded expiration to 1 month, tweak as needed.
    private const val ONE_MONTH_EXPIRATION = 30 * 24 * 60 * 60 * 1000L

    enum class TokenState {
        Valid, Expired, Invalid
    }

    /**
     * Verify the authorization token from the headers.
     * If invalid then the Unauthorized response is sent.
     */
    fun verify(call: ApplicationCall) {
        val appSettings = SettingsProvider.get

        if (appSettings.jwt.isEnabled) {
            if (getState(call) != TokenState.Valid) {
                runBlocking {
                    call.respond(HttpStatusCode.Unauthorized, "Unauthorized: Error while authorizing.")
                }
            }
        }
    }

    /**
     * Returns the current [TokenState] from the header authorization token.
     */
    fun getState(call: ApplicationCall): TokenState {
        val appSettings = SettingsProvider.get

        return try {
            val token = fromHeader(call)
            val algorithm: Algorithm = Algorithm.HMAC256(appSettings.jwt.secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            verifier.verify(JWT.decode(token))
            TokenState.Valid
        } catch (e: TokenExpiredException) {
            TokenState.Expired
        } catch (e: Exception) {
            logger.error("Unauthorized: Error while authorizing. $e")
            TokenState.Invalid
        }
    }

    /**
     * Returns the current authorization token from the headers.
     */
    fun fromHeader(call: ApplicationCall): String {
        val authHeader = call.request.headers.entries().find {
            it.key.equals("Authorization", ignoreCase = true)
        }?.value?.get(0) ?: ""

        val bearerPrefix = "Bearer"
        val bearerIndex = authHeader.indexOf(bearerPrefix, ignoreCase = true)
        if (bearerIndex == -1) {
            throw IllegalArgumentException("Missing Bearer in Authorization header.")
        }

        return authHeader.substring(bearerIndex + bearerPrefix.length).trim()
    }

    /**
     * Generate a new authorization token.
     */
    fun generate(): String {
        val appSettings = SettingsProvider.get
        val expirationDate = Date(System.currentTimeMillis() + ONE_MONTH_EXPIRATION)

        return JWT.create()
            .withAudience(appSettings.jwt.audience)
            .withIssuer(appSettings.jwt.issuer)
            .withExpiresAt(expirationDate)
            .sign(Algorithm.HMAC256(appSettings.jwt.secretKey))
    }
}
