/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.utils

import com.kcrud.settings.SettingsProvider

internal object NetworkUtils {
    private val tracer = Tracer.create<NetworkUtils>()

    fun logEndpoint(reason: String, endpoint: String) {
        logEndpoints(reason = reason, endpoints = listOf(endpoint))
    }

    fun logEndpoints(reason: String, endpoints: List<String>) {
        var host = SettingsProvider.deployment.host
        host = if (host == "0.0.0.0") "localhost" else host

        val port = SettingsProvider.deployment.port
        val protocol = if (port == 443) "https" else "http"
        val url = "$protocol://$host:$port"

        if (endpoints.size == 1) {
            tracer.info("$reason: $url/${endpoints[0]}")
        } else {
            tracer.info("$reason:")
            endpoints.forEach { endpoint ->
                tracer.info("$url/$endpoint")
            }
        }
    }
}
