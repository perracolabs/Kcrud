package com.kcrud.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

/**
 * Security class responsible for verifying JWT tokens.
 *
 * This class contains the logic for verifying JWT tokens using HMAC256 algorithm.
 * If the token is invalid or any exception occurs, an UnauthorizedException will be thrown.
 */
class Security {
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    fun verifyToken(call: ApplicationCall) {
        val settings = call.application.appSettings()

        if (!settings.jwt.isEnabled)
            return

        try {
            val token = getAuthorizationToken(call)
            val algorithm: Algorithm = Algorithm.HMAC256(settings.jwt.secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).build()
            verifier.verify(JWT.decode(token))
        } catch (e: Exception) {
            println(e.message)
            logger.error("Unauthorized: Error while authorizing.", e)

            runBlocking {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized: Error while authorizing.")
            }
        }
    }

    private fun getAuthorizationToken(call: ApplicationCall): String {
        val authHeader = call.request.headers.entries().find {
            it.key.equals("Authorization", ignoreCase = true)
        }?.value?.get(0) ?: ""

        val bearerPrefix = "Bearer "
        val bearerIndex = authHeader.indexOf(bearerPrefix, ignoreCase = true)
        if (bearerIndex == -1) {
            throw IllegalArgumentException("Missing Bearer in Authorization header.")
        }

        return authHeader.substring(bearerIndex + bearerPrefix.length)
    }
}
