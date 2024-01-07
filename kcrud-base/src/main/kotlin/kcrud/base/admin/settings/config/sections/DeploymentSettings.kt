/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.settings.config.sections

import kcrud.base.admin.types.EnvironmentType

/**
 * Contains settings related to how the application is deployed.
 *
 * @property type The deployment type. Not to be confused with the development mode.
 * @property useSecureConnection Whether to use a secure connection or not.
 * @property port The network port the server listens on.
 * @property sslPort The network port the server listens on for secure connections.
 * @property host The network address the server is bound to.
 * @property apiVersion The API version.
 * @property workingDir The working directory where files are stored.
 */
data class DeploymentSettings(
    val type: EnvironmentType,
    val useSecureConnection: Boolean,
    val port: Int,
    val sslPort: Int,
    val host: String,
    val apiVersion: String,
    val workingDir: String
)
