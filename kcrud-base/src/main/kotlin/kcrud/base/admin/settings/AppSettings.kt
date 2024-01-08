/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.settings

import io.ktor.server.application.*
import kcrud.base.admin.settings.annotation.ConfigurationAPI
import kcrud.base.admin.settings.config.ConfigurationCatalog
import kcrud.base.admin.settings.config.parser.ConfigClassMap
import kcrud.base.admin.settings.config.parser.ConfigurationParser
import kcrud.base.admin.settings.config.sections.*
import kcrud.base.admin.settings.config.sections.security.SecuritySettings
import kcrud.base.utils.Tracer

/**
 * Singleton providing the configuration settings throughout the application.
 *
 * This class serves as the central point for accessing all configuration settings in a type-safe manner.
 */
object AppSettings {
    @Volatile
    private lateinit var configuration: ConfigurationCatalog

    val server: ServerSettings get() = configuration.server
    val deployment: DeploymentSettings get() = configuration.deployment
    val cors: CorsSettings get() = configuration.cors
    val database: DatabaseSettings get() = configuration.database
    val docs: DocsSettings get() = configuration.docs
    val graphql: GraphQLSettings get() = configuration.graphql
    val security: SecuritySettings get() = configuration.security

    @OptIn(ConfigurationAPI::class)
    @Synchronized
    fun load(context: Application) {
        if (AppSettings::configuration.isInitialized)
            return

        val tracer = Tracer<AppSettings>()
        tracer.debug("Loading application configuration.")

        // Map connecting configuration paths.
        // Where the first argument is the path to the configuration section,
        // the second argument is the name of the constructor argument in the
        // ConfigurationCatalog class, and the third argument is the data class
        // that will be instantiated with the configuration values.
        val configMappings = listOf(
            ConfigClassMap(path = "ktor", argument = "server", kClass = ServerSettings::class),
            ConfigClassMap(path = "ktor.deployment", argument = "deployment", kClass = DeploymentSettings::class),
            ConfigClassMap(path = "ktor.cors", argument = "cors", kClass = CorsSettings::class),
            ConfigClassMap(path = "ktor.database", argument = "database", kClass = DatabaseSettings::class),
            ConfigClassMap(path = "ktor.docs", argument = "docs", kClass = DocsSettings::class),
            ConfigClassMap(path = "ktor.graphql", argument = "graphql", kClass = GraphQLSettings::class),
            ConfigClassMap(path = "ktor.security", argument = "security", kClass = SecuritySettings::class)
        )

        configuration = ConfigurationParser.parse(
            configuration = context.environment.config,
            mappings = configMappings
        )

        tracer.debug("Configuration loaded successfully.")
    }
}
