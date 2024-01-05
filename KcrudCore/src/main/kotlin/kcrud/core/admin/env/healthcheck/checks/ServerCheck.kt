/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.admin.env.healthcheck.checks

import kcrud.core.admin.env.healthcheck.annotation.HealthCheckAPI
import kcrud.core.admin.settings.AppSettings
import kcrud.core.admin.types.EnvironmentType
import kcrud.core.utils.DateTimeUtils
import kcrud.core.utils.NetworkUtils
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Suppress("unused")
@HealthCheckAPI
@Serializable
data class ServerCheck(
    val errors: MutableList<String> = mutableListOf(),
    val machineId: Int = AppSettings.server.machineId,
    val environmentType: EnvironmentType = AppSettings.deployment.type,
    val developmentModeEnabled: Boolean = AppSettings.server.development,
    val protocol: String = NetworkUtils.getProtocol().name,
    val host: String = NetworkUtils.getServerUrl().toString(),
    val allowedHosts: List<String> = AppSettings.cors.allowedHosts,
    val utc: LocalDateTime = DateTimeUtils.currentUTCDateTime(),
    val local: LocalDateTime = DateTimeUtils.utcToLocal(utc = utc)
) {
    init {
        val className = this::class.simpleName

        if (environmentType == EnvironmentType.PROD) {
            if (allowedHosts.isEmpty() or allowedHosts.contains("*")) {
                errors.add("$className. Allowing all hosts in '$environmentType' environment.")
            }

            if (developmentModeEnabled) {
                errors.add("$className. Development mode is enabled in '$environmentType' environment.")
            }

            if (!NetworkUtils.isSecureProtocol(protocol = protocol)) {
                errors.add("$className. Using insecure '$protocol' protocol in '$environmentType' environment.")
            }
        }
    }
}
