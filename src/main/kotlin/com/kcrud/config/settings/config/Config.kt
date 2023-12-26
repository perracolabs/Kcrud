/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.config

import com.kcrud.config.settings.config.sections.*

/**
 * Represents the application's configuration settings as a structured, easily accessible model.
 *
 * For accurate mapping, the names of nested data classes must correspond directly with
 * their configuration section names. Similarly, property names within these classes
 * must exactly match the key names in their respective configuration sections.
 */
internal data class Config(
    val server: ServerSettings,
    val deployment: DeploymentSettings,
    val cors: CorsSettings,
    val database: DatabaseSettings,
    val docs: DocsSettings,
    val graphql: GraphQLSettings,
    val security: SecuritySettings
)
