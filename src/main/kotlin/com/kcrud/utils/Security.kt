package com.kcrud.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking

/**
 * Security class responsible for verifying JWT tokens.
 *
 * This class contains the logic for verifying JWT tokens using HMAC256 algorithm.
 * If the token is invalid or any exception occurs, an UnauthorizedException will be thrown.
 */
class Security {

    /**
     * Verify the JWT token from the incoming request.
     *
     * @param call The ApplicationCall from the incoming request.
     */
    fun verifyToken(call: ApplicationCall) {
        val settings = call.application.appSettings()

        if (!settings.jwt.isEnabled)
            return

        try {
            val algorithm: Algorithm = Algorithm.HMAC256(settings.jwt.secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).build()

            val authHeader = call.request.headers.entries().find {
                it.key.equals("Authorization", ignoreCase = true)
            }?.value?.get(0) ?: ""

            val token = authHeader.split("Bearer ").last()
            verifier.verify(JWT.decode(token))
        } catch (e: Exception) {
            println(e.message)

            runBlocking {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized: Error while authorizing.")
            }
        }
    }
}
