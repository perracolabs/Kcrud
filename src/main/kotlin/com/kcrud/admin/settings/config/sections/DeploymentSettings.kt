/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.settings.config.sections

import com.kcrud.admin.env.types.DeploymentType

/**
 * Contains settings related to how the application is deployed.
 *
 * @property type The deployment type. Not to be confused with the development mode.
 * @property port The network port the server listens on.
 * @property host The network address the server is bound to.
 * @property apiVersion The API version.
 */
internal data class DeploymentSettings(
    val type: DeploymentType,
    val port: Int,
    val host: String,
    val apiVersion: String
)
