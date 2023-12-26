/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.env.healthcheck.checks

import com.kcrud.config.env.types.DeploymentType
import com.kcrud.config.settings.AppSettings
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
data class ApplicationCheck(
    val errors: MutableList<String> = mutableListOf(),
    val apiVersion: String = AppSettings.deployment.apiVersion,
    val docsEnabled: Boolean = AppSettings.docs.isEnabled
) {
    init {
        val className = this::class.simpleName
        val deploymentType = AppSettings.deployment.type

        if (deploymentType == DeploymentType.PROD) {
            if (docsEnabled) {
                errors.add("$className. Docs are enabled in '$deploymentType' environment.")
            }
        }
    }
}