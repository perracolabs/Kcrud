package com.kcrud.utils

import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


/**
 * Application extension function to retrieve the singleton instance of AppSettings.
 */
fun Application.appSettings(): AppSettings {
    return AppSettings(environment.config)
}

/**
 * Represents the application's global settings, as parsed from a configuration file.
 *
 * @property global Configurations specific to main global environment.
 * @property deployment Configurations for the server's deployment settings.
 * @property jwt Configurations for JWT-based authentication.
 * @property basicAuth Configurations for basic HTTP authentication.
 */
data class AppSettings(
    val global: Global,
    val deployment: Deployment,
    val jwt: Jwt,
    val basicAuth: BasicAuth
) {
    /**
     * Contains the main global configuration settings.
     *
     * @property development Indicates whether development mode is active.
     */
    data class Global(val development: Boolean)

    /**
     * Contains settings related to how the application is deployed.
     *
     * @property port The network port the server listens on.
     * @property host The network address the server is bound to.
     */
    data class Deployment(val port: Int, val host: String)

    /**
     * Jwt class holds settings related to JWT authentication.
     *
     * @property isEnabled Flag to enable/disable JWT authentication.
     * @property audience Intended recipients of the JWT.
     * @property issuer Entity that issues the JWT.
     * @property realm Security realm for the JWT authentication.
     * @property secretKey Secret key for signing the JWT.
     */
    data class Jwt(val isEnabled: Boolean, val audience: String, val issuer: String, val realm: String, val secretKey: String)

    /**
     * Basic authentication class settings.
     *
     * @property isEnabled Flag to enable/disable basic authentication.
     * @property providerName Name of the basic authentication provider.
     * @property realm Security realm for the basic authentication.
     * @property username Username for basic authentication.
     * @property password Password for basic authentication.
     */
    data class BasicAuth(val isEnabled: Boolean, val providerName: String, val realm: String, val username: String, val password: String)

    companion object {
        // Singleton instance of AppSettings.
        private var instance: AppSettings? = null

        /**
         * Factory method to create or return the instance of AppSettings.
         *
         * @param config Configuration from which the application's settings must be parsed.
         * @return Singleton instance of AppSettings, populated with the parsed configuration data.
         */
        operator fun invoke(config: ApplicationConfig): AppSettings {
            return instance ?: synchronized(this) {
                instance ?: createSettings(config).also { instance = it }
            }
        }

        /**
         * Internal method for creating an AppSettings instance from parsed configuration data.
         *
         * @param config Configuration data parsed from the application's settings file.
         * @return A new AppSettings object populated with the parsed configuration data.
         */
        private fun createSettings(config: ApplicationConfig): AppSettings {
            val global = instantiateConfig(config, "ktor", Global::class)
            val deployment = instantiateConfig(config, "ktor.deployment", Deployment::class)
            val jwt = instantiateConfig(config, "ktor.jwt", Jwt::class)
            val basicAuth = instantiateConfig(config, "ktor.basic_auth", BasicAuth::class)
            return AppSettings(global = global, deployment = deployment, jwt = jwt, basicAuth = basicAuth)
        }

        /**
         * Instantiates a class with a constructor that takes a set of named parameters.
         *
         * @param config Configuration data with the settings to be parsed.
         * @param keyPath The target path section to be parsed in the configuration.
         * @param kClass The KClass of the type to be instantiated.
         * @return An instance of the specified class, populated with the provided parameter values.
         * @throws IllegalArgumentException If any of arguments from the target class is missing in the configuration.
         */
        private fun <T : Any> instantiateConfig(config: ApplicationConfig, keyPath: String, kClass: KClass<T>): T {
            val configMap: Map<String, Any?> = config.config(keyPath).toMap()
            val constructor = kClass.primaryConstructor!!
            val arguments = constructor.parameters.associateWith { parameter ->
                configMap[parameter.name] ?: throw IllegalArgumentException("Missing configuration value for ${parameter.name}")
            }

            return constructor.callBy(args = arguments)
        }
    }
}
