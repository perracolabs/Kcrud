/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.utils

import com.kcrud.admin.settings.AppSettings
import io.ktor.http.*

/**
 * Utility object for network-related functionalities.
 */
internal object NetworkUtils {
    private val tracer = Tracer<NetworkUtils>()

    private const val LISTEN_ALL_IPS: String = "0.0.0.0"
    private const val SECURE_PORT: Int = 443
    private val SECURE_PROTOCOL: URLProtocol = URLProtocol.HTTPS
    private val INSECURE_PROTOCOL: URLProtocol = URLProtocol.HTTP

    /**
     * Logs multiple endpoints with a specified reason.
     *
     * Logs are formatted to full URLs.
     *
     * @param reason A description of why these endpoints are being logged, for context.
     * @param endpoints The list of endpoints to be logged.
     */
    fun logEndpoints(reason: String, endpoints: List<String>) {
        val url: Url? = getServerUrl()
        tracer.info("$reason:")
        endpoints.forEach { endpoint ->
            tracer.info("$url/$endpoint")
        }
    }

    fun getServerUrl(): Url? {
        val host = AppSettings.deployment.host
        var url: Url? = null

        if (host != LISTEN_ALL_IPS) {
            val port = AppSettings.deployment.port
            val protocol = getProtocol()
            url = URLBuilder(protocol = protocol, host = host, port = port).build()
        }

        return url
    }

    fun getProtocol(): URLProtocol {
        return if (AppSettings.deployment.port == SECURE_PORT) SECURE_PROTOCOL else INSECURE_PROTOCOL
    }

    fun isSecureProtocol(protocol: String): Boolean {
        return protocol == SECURE_PROTOCOL.name
    }
}
