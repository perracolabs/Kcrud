package com.kcrud.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.utils.appSettings
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


/**
 * Configures the Basic and JWT-based authentications.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 *
 * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
 */
fun Application.configureAuthentication() {

    val appSettings = appSettings()

    authentication {

        // JWT-based authentications.
        jwt {
            realm = appSettings.jwt.realm

            // Build and set the JWT verifier with the configured settings.
            verifier(
                JWT
                    .require(Algorithm.HMAC256(appSettings.jwt.secretKey))
                    .withAudience(appSettings.jwt.audience)
                    .withIssuer(appSettings.jwt.issuer)
                    .build()
            )

            // Validate the credentials; check if the audience in the token matches the expected audience.
            validate { credential ->
                if (credential.payload.audience.contains(appSettings.jwt.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }

        // Basic authentication.
        basic(appSettings.basicAuth.providerName) {
            realm = appSettings.basicAuth.realm

            validate { credentials ->
                if (credentials.name == appSettings.basicAuth.username && credentials.password == appSettings.basicAuth.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
