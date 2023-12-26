/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings

import com.kcrud.config.settings.config.Config
import com.kcrud.config.settings.config.ConfigAPI
import com.kcrud.config.settings.config.ConfigParser
import com.kcrud.config.settings.config.sections.*
import com.kcrud.utils.Tracer
import io.ktor.server.application.*

/**
 * Singleton providing the configuration settings throughout the application.
 *
 * This class serves as the central point for accessing all configuration settings in a type-safe manner.
 */
internal object AppSettings {
    @Volatile
    private lateinit var config: Config

    val server: ServerSettings get() = config.server
    val deployment: DeploymentSettings get() = config.deployment
    val cors: CorsSettings get() = config.cors
    val database: DatabaseSettings get() = config.database
    val docs: DocsSettings get() = config.docs
    val graphql: GraphQLSettings get() = config.graphql
    val security: SecuritySettings get() = config.security

    @Synchronized
    fun load(context: Application) {
        if (AppSettings::config.isInitialized)
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

        @OptIn(ConfigAPI::class)
        config = ConfigParser.parse(
            configuration = context.environment.config,
            mappings = configMappings
        )

        tracer.debug("Configuration loaded successfully.")
    }
}
