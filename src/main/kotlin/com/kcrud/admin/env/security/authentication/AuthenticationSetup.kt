/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.env.security.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kcrud.admin.settings.AppSettings
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Security class responsible for the configuration of the app authentication,
 * for both Basic and JWT.
 */
internal object AuthenticationSetup {

    /**
     * Configures JWT authentication.
     *
     * See: [Ktor JWT Authentication Documentation](https://ktor.io/docs/jwt.html)
     */
    fun configureJwt(config: AuthenticationConfig) {

        config.jwt {
            realm = AppSettings.security.jwt.realm

            // Build and set the JWT verifier with the configured settings.
            verifier(
                JWT
                    .require(Algorithm.HMAC256(AppSettings.security.jwt.secretKey))
                    .withAudience(AppSettings.security.jwt.audience)
                    .withIssuer(AppSettings.security.jwt.issuer)
                    .build()
            )

            // Validate the credentials; check if the audience in the token matches the expected audience.
            validate { credential ->
                if (credential.payload.audience.contains(AppSettings.security.jwt.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    /**
     * Configures basic authentication.
     *
     * See: [Basic Authentication Documentation](https://ktor.io/docs/basic.html)
     */
    fun configureBasicAuth(config: AuthenticationConfig) {

        config.basic(name = AppSettings.security.basicAuth.providerName) {
            realm = AppSettings.security.basicAuth.realm

            validate { credentials ->
                val isValid = BasicCredentials.verify(username = credentials.name, password = credentials.password)

                if (isValid) {
                    UserIdPrincipal(name = credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
