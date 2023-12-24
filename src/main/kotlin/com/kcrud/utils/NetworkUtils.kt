/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.utils

import com.kcrud.settings.SettingsProvider
import com.kcrud.system.Tracer

/**
 * Utility object for network-related functionalities.
 */
internal object NetworkUtils {
    private val tracer = Tracer<NetworkUtils>()

    private const val LISTEN_ALL_IPS = "0.0.0.0"
    private const val SECURE_PROTOCOL = "https"
    private const val INSECURE_PROTOCOL = "http"
    private const val SECURE_PORT = 443

    /**
     * Logs multiple endpoints with a specified reason.
     *
     * Logs are formatted to full URLs.
     *
     * @param reason A description of why these endpoints are being logged, for context.
     * @param endpoints The list of endpoints to be logged.
     */
    fun logEndpoints(reason: String, endpoints: List<String>) {
        val url = getServerUrl()

        if (endpoints.size == 1) {
            tracer.info("$reason: $url/${endpoints[0]}")
        } else {
            tracer.info("$reason:")
            endpoints.forEach { endpoint ->
                tracer.info("$url/$endpoint")
            }
        }
    }

    fun getServerUrl(): String {
        val host = SettingsProvider.deployment.host
        var url = ""

        if (host != LISTEN_ALL_IPS) {
            val port = SettingsProvider.deployment.port
            val protocol = getProtocol()
            url = "$protocol://$host:$port"
        }

        return url
    }

    fun getProtocol(): String {
        return if (SettingsProvider.deployment.port == SECURE_PORT) SECURE_PROTOCOL else INSECURE_PROTOCOL
    }
}
