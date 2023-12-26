/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.utils

import com.kcrud.config.settings.AppSettings

/**
 * Utility object for network-related functionalities.
 */
internal object NetworkUtils {
    private val tracer = Tracer<NetworkUtils>()

    private const val LISTEN_ALL_IPS = "0.0.0.0"
    private const val SECURE_PORT = 443
    private const val SECURE_PROTOCOL = "https"
    const val INSECURE_PROTOCOL = "http"

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
        tracer.info("$reason:")
        endpoints.forEach { endpoint ->
            tracer.info("$url/$endpoint")
        }
    }

    fun getServerUrl(): String {
        val host = AppSettings.deployment.host
        var url = ""

        if (host != LISTEN_ALL_IPS) {
            val port = AppSettings.deployment.port
            val protocol = getProtocol()
            url = "$protocol://$host:$port"
        }

        return url
    }

    fun getProtocol(): String {
        return if (AppSettings.deployment.port == SECURE_PORT) SECURE_PROTOCOL else INSECURE_PROTOCOL
    }
}
