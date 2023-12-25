/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system.healthcheck.checks

import com.kcrud.settings.AppSettings
import com.kcrud.utils.DeploymentType
import com.kcrud.utils.NetworkUtils
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class ServerCheck(
    val errors: MutableList<String> = mutableListOf(),
    val machineId: Int = AppSettings.server.machineId,
    val deploymentType: DeploymentType = AppSettings.deployment.type,
    val developmentModeEnabled: Boolean = AppSettings.server.development,
    val protocol: String = NetworkUtils.getProtocol(),
    val host: String = NetworkUtils.getServerUrl(),
    val allowedHosts: List<String> = AppSettings.cors.allowedHosts,
    val utc: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val local: LocalDateTime = utc.toInstant(TimeZone.UTC).toLocalDateTime(TimeZone.currentSystemDefault())
) {
    init {
        val className = this::class.simpleName

        if (deploymentType == DeploymentType.PROD) {
            if (allowedHosts.isEmpty() or allowedHosts.contains("*"))
                errors.add("$className. Allowing all hosts in '$deploymentType' environment.")

            if (developmentModeEnabled)
                errors.add("$className. Development mode is enabled in '$deploymentType' environment.")

            if (protocol == NetworkUtils.INSECURE_PROTOCOL) errors.add("$className. Using ${NetworkUtils.INSECURE_PROTOCOL} protocol in '$deploymentType' environment.")
        }
    }
}
