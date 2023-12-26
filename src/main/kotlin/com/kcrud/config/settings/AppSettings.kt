/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings

import com.kcrud.config.settings.configuration.Configuration
import com.kcrud.config.settings.configuration.ConfigurationAPI
import com.kcrud.config.settings.configuration.ConfigurationParser
import com.kcrud.config.settings.configuration.sections.*
import com.kcrud.config.settings.configuration.sections.security.SecuritySettings
import com.kcrud.utils.Tracer
import io.ktor.server.application.*

/**
 * Singleton providing the configuration settings throughout the application.
 *
 * This class serves as the central point for accessing all configuration settings in a type-safe manner.
 */
internal object AppSettings {
    @Volatile
    private lateinit var configuration: Configuration

    val server: ServerSettings get() = configuration.server
    val deployment: DeploymentSettings get() = configuration.deployment
    val cors: CorsSettings get() = configuration.cors
    val database: DatabaseSettings get() = configuration.database
    val docs: DocsSettings get() = configuration.docs
    val graphql: GraphQLSettings get() = configuration.graphql
    val security: SecuritySettings get() = configuration.security

    @Synchronized
    fun load(context: Application) {
        if (AppSettings::configuration.isInitialized)
            return

        val tracer = Tracer<AppSettings>()
        tracer.debug("Loading application configuration.")

        // Map connecting configuration paths (e.g., "ktor", "ktor.database") to pairs
        // of parameter names in the Config class and their corresponding data classes.
        // This enables dynamic parsing of configuration sections and instantiation
        // of the associated settings classes.
        val configMappings = mapOf(
            "ktor" to Pair("server", ServerSettings::class),
            "ktor.deployment" to Pair("deployment", DeploymentSettings::class),
            "ktor.cors" to Pair("cors", CorsSettings::class),
            "ktor.database" to Pair("database", DatabaseSettings::class),
            "ktor.docs" to Pair("docs", DocsSettings::class),
            "ktor.graphql" to Pair("graphql", GraphQLSettings::class),
            "ktor.security" to Pair("security", SecuritySettings::class)
        )

        @OptIn(ConfigurationAPI::class)
        configuration = ConfigurationParser.parse(
            configuration = context.environment.config,
            mappings = configMappings
        )

        tracer.debug("Configuration loaded successfully.")
    }
}
