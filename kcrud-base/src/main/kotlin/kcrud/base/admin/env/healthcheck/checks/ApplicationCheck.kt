/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.env.healthcheck.checks

import kcrud.base.admin.env.healthcheck.annotation.HealthCheckAPI
import kcrud.base.admin.settings.AppSettings
import kcrud.base.admin.types.EnvironmentType
import kotlinx.serialization.Serializable

@Suppress("unused")
@HealthCheckAPI
@Serializable
data class ApplicationCheck(
    val errors: MutableList<String> = mutableListOf(),
    val apiVersion: String = AppSettings.deployment.apiVersion,
    val docsEnabled: Boolean = AppSettings.docs.isEnabled
) {
    init {
        val className = this::class.simpleName
        val deploymentType = AppSettings.deployment.type

        if (deploymentType == EnvironmentType.PROD) {
            if (docsEnabled) {
                errors.add("$className. Docs are enabled in '$deploymentType' environment.")
            }
        }
    }
}
