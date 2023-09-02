import io.ktor.server.application.*
import io.ktor.server.config.*


/**
 * Application extension function to retrieve the singleton instance of AppSettings.
 */
fun Application.appSettings(): AppSettings {
    return AppSettings(environment.config)
}

/**
 * Holds the configurations settings for the application.
 *
 * @property development The development environment settings.
 * @property deployment The application's deployment settings.
 * @property jwt The JWT authentication settings.
 */
data class AppSettings(
    val development: Development,
    val deployment: Deployment,
    val jwt: Jwt,
    val basicAuth: BasicAuth
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

    /**
     * Basic authentication class settings.
     * @property isEnabled Indicates if basic authentication is enabled.
     * @property providerName Specifies the authentication provider name.
     * @property realm Specifies the realm for the basic authentication.
     */
    data class BasicAuth(val isEnabled: Boolean, val providerName: String, val realm: String, val username: String, val password: String)

    companion object {
        // Singleton instance of AppSettings
        private var instance: AppSettings? = null

        /**
         * Factory method to create the singleton instance of AppSettings.
         * @param config The ApplicationConfig object from which settings are parsed.
         * @return The singleton instance of AppSettings with values populated from the provided config.
         */
        operator fun invoke(config: ApplicationConfig): AppSettings {
            return instance ?: synchronized(this) {
                instance ?: createSettings(config).also { instance = it }
            }
        }

        /**
         * Create the instance of AppSettings by parsing the configuration.
         * @param config The ApplicationConfig object from which settings are parsed.
         * @return An instance of AppSettings with values populated from the provided config.
         */
        private fun createSettings(config: ApplicationConfig): AppSettings {
            val developmentConfig = config.property("ktor.development").getString().toBoolean()
            val deployConfig = config.config("ktor.deployment")
            val jwtConfig = config.config("ktor.jwt")
            val basicAuthConfig = config.config("ktor.basic_auth")

            return AppSettings(
                Development(isEnabled = developmentConfig),
                Deployment(
                    port = deployConfig.property("port").getString().toInt(),
                    host = deployConfig.property("host").getString()
                ),
                Jwt(
                    isEnabled = jwtConfig.property("enabled").getString().toBoolean(),
                    audience = jwtConfig.property("audience").getString(),
                    issuer = jwtConfig.property("issuer").getString(),
                    realm = jwtConfig.property("realm").getString(),
                    secretKey = jwtConfig.property("secret-key").getString()
                ),
                BasicAuth(
                    isEnabled = basicAuthConfig.property("enabled").getString().toBoolean(),
                    providerName = basicAuthConfig.property("provider-name").getString(),
                    realm = basicAuthConfig.property("realm").getString(),
                    username = basicAuthConfig.property("username").getString(),
                    password = basicAuthConfig.property("password").getString()
                )
            )
        }
    }
}
