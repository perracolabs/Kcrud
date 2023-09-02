package com.kcrud.app.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.utils.AppConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Configures the JWT-based authentication.
 *
 * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
 */
fun Application.configureAuthentication() {

    val appConfig = AppConfig(config = environment.config)

    // Configure the JWT authentication feature.
    authentication {

        jwt {
            realm = appConfig.jwt.realm

            // Build and set the JWT verifier with the configured settings.
            verifier(
                JWT
                    .require(Algorithm.HMAC256(appConfig.jwt.secretKey))
                    .withAudience(appConfig.jwt.audience)
                    .withIssuer(appConfig.jwt.issuer)
                    .build()
            )

            // Validate the credentials; check if the audience in the token matches the expected audience.
            validate { credential ->
                if (credential.payload.audience.contains(appConfig.jwt.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
