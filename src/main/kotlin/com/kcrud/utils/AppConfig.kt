package com.kcrud.utils

import io.ktor.server.config.*

/**
 * Holds the configurations settings for the application.
 *
 * @property development The development environment settings.
 * @property deployment The application's deployment settings.
 * @property jwt The JWT authentication settings.
 */
data class AppConfig(
    val development: Development,
    val deployment: Deployment,
    val jwt: Jwt
) {
    /**
     * Development class holds settings specific to the development environment.
     * @property isEnabled Indicates if the development mode is enabled.
     */
    data class Development(val isEnabled: Boolean)

    /**
     * Deployment class holds settings related to the deployment of the application.
     * @property port Specifies the port on which the application runs.
     * @property host Specifies the host of the application.
     */
    data class Deployment(val port: Int, val host: String)

    /**
     * Jwt class holds settings related to JWT authentication.
     * @property isEnabled Indicates if JWT authentication is enabled.
     * @property audience Specifies the audience for the JWT.
     * @property issuer Specifies the issuer of the JWT.
     * @property realm Specifies the realm for the JWT.
     * @property secretKey Holds the secret key for signing the JWT.
     */
    data class Jwt(val isEnabled: Boolean, val audience: String, val issuer: String, val realm: String, val secretKey: String)

    companion object {
        /**
         * Factory method to create the class instance from an ApplicationConfig.
         * @param config The ApplicationConfig object from which settings are parsed.
         * @return An instance of AppConfig with values populated from the provided config.
         */
        operator fun invoke(config: ApplicationConfig): AppConfig {
            val developmentConfig = config.property("ktor.development").getString().toBoolean()
            val deployConfig = config.config("ktor.deployment")
            val jwtConfig = config.config("ktor.jwt")

            return AppConfig(
                Development(isEnabled = developmentConfig),
                Deployment(
                    port = deployConfig.property("port").getString().toInt(),
                    host = deployConfig.property("host").getString()
                ),
                Jwt(
                    isEnabled = jwtConfig.property("is_enabled").getString().toBoolean(),
                    audience = jwtConfig.property("audience").getString(),
                    issuer = jwtConfig.property("issuer").getString(),
                    realm = jwtConfig.property("realm").getString(),
                    secretKey = jwtConfig.property("secret_key").getString()
                )
            )
        }
    }
}
