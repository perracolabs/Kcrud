/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.admin.settings.config.sections

import kcrud.core.admin.types.EnvironmentType

/**
 * Contains settings related to how the application is deployed.
 *
 * @property type The deployment type. Not to be confused with the development mode.
 * @property port The network port the server listens on.
 * @property host The network address the server is bound to.
 * @property apiVersion The API version.
 * @property workingDir The working directory where files are stored.
 */
data class DeploymentSettings(
    val type: EnvironmentType,
    val port: Int,
    val host: String,
    val apiVersion: String,
    val workingDir: String
)
