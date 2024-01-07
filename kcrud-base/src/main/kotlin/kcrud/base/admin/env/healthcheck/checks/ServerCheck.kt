/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.env.healthcheck.checks

import io.ktor.server.application.*
import io.ktor.server.request.*
import kcrud.base.admin.env.healthcheck.annotation.HealthCheckAPI
import kcrud.base.admin.settings.AppSettings
import kcrud.base.admin.types.EnvironmentType
import kcrud.base.utils.DateTimeUtils
import kcrud.base.utils.NetworkUtils
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@HealthCheckAPI
@Serializable
data class ServerCheck(
    @kotlinx.serialization.Transient
    val call: ApplicationCall? = null,

    val errors: MutableList<String> = mutableListOf(),
    val runtime: Runtime = Runtime(call = call),
    val configured: Configured = Configured(),
    val utc: LocalDateTime = DateTimeUtils.currentUTCDateTime(),
    val local: LocalDateTime = DateTimeUtils.utcToLocal(utc = utc),
) {
    @Suppress("unused")
    @Serializable
    data class Runtime(
        @kotlinx.serialization.Transient
        val call: ApplicationCall? = null
    ) {
        val serverHost: String? = call?.request?.local?.serverHost
        val serverPort: Int? = call?.request?.local?.serverPort
        val localHost: String? = call?.request?.local?.localHost
        val localPort: Int? = call?.request?.local?.localPort
        val remoteHostHost: String? = call?.request?.local?.remoteHost
        val remoteAddress: String? = call?.request?.local?.remoteAddress
        val remotePort: Int? = call?.request?.local?.remotePort
        val httpVersion: String? = call?.request?.httpVersion
        val scheme: String? = call?.request?.local?.scheme
    }

    @Serializable
    data class Configured(
        val machineId: Int = AppSettings.server.machineId,
        val environmentType: EnvironmentType = AppSettings.deployment.type,
        val developmentModeEnabled: Boolean = AppSettings.server.development,
        val useSecureConnection: Boolean = AppSettings.deployment.useSecureConnection,
        val protocol: String = NetworkUtils.getProtocol().name,
        val port: Int = AppSettings.deployment.port,
        val sslPort: Int = AppSettings.deployment.sslPort,
        val host: String = NetworkUtils.getServerUrl().toString(),
        val allowedHosts: List<String> = AppSettings.cors.allowedHosts,
    )

    init {
        val className = this::class.simpleName

        if (configured.environmentType == EnvironmentType.PROD) {
            if (configured.allowedHosts.isEmpty() or configured.allowedHosts.contains("*")) {
                errors.add("$className. Allowing all hosts. '${configured.environmentType}'.")
            }

            if (configured.developmentModeEnabled) {
                errors.add("$className. Development mode is enabled. '${configured.environmentType}'.")
            }

            if (!NetworkUtils.isSecureProtocol(protocol = configured.protocol)) {
                errors.add(
                    "$className. Configured insecure '${configured.protocol}' protocol. '${configured.environmentType}'."
                )
            }

            runtime.scheme?.let { scheme ->
                if (!NetworkUtils.isSecureProtocol(protocol = scheme)) {
                    errors.add(
                        "$className. Running with insecure '${scheme}' protocol. '${configured.environmentType}'."
                    )
                }
            }

            if (configured.port == configured.sslPort) {
                errors.add(
                    "$className. Secure and insecure ports are the same: ${configured.port}. " +
                            "${configured.environmentType}."
                )
            }

            if (configured.useSecureConnection) {
                val configuredPort = listOf(configured.sslPort)
                if (NetworkUtils.isInsecurePort(ports = configuredPort)) {
                    errors.add(
                        "$className. Configured port is not secure or not set. " +
                                "Port: $configuredPort. ${configured.environmentType}."
                    )
                }

                val runtimePorts: List<Int?> = listOf(runtime.serverPort, runtime.localPort, runtime.remotePort)
                if (NetworkUtils.isInsecurePort(ports = runtimePorts)) {
                    errors.add(
                        "$className. Runtime ports are not secure or not set. " +
                                "Port: $configuredPort. ${configured.environmentType}."
                    )
                }
            } else {
                if (configured.port == 0) {
                    errors.add("$className. Insecure port is not set. ${configured.environmentType}.")
                }
            }
        }
    }
}
