/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.settings.config.sections

/**
 * Contains the server root-level main configuration settings.
 *
 * @property development Ktor flag indicating whether development mode is enabled.
 *                       This makes Ktor to activate development concrete features.
 *                       See: [Development Mode](https://ktor.io/docs/development-mode.html)
 * @property machineId The unique machine ID. Used for generating unique IDs for call traceability.
 */
internal data class ServerSettings(
    val development: Boolean,
    val machineId: Int
)
