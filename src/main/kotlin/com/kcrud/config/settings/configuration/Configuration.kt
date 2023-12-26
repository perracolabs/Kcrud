/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.configuration

import com.kcrud.config.settings.configuration.sections.*
import com.kcrud.config.settings.configuration.sections.security.SecuritySettings

/**
 * Represents the application's configuration settings as a structured, easily accessible model.
 *
 * For accurate mapping, the property names of nested data classes must correspond directly
 * with their configuration section names.
 */
internal data class Configuration(
    val server: ServerSettings,
    val deployment: DeploymentSettings,
    val cors: CorsSettings,
    val database: DatabaseSettings,
    val docs: DocsSettings,
    val graphql: GraphQLSettings,
    val security: SecuritySettings
)
