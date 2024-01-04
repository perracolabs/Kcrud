/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.plugins

import com.kcrud.core.admin.env.security.snowflake.SnowflakeFactory
import com.kcrud.core.admin.env.security.user.ContextUser
import com.kcrud.core.admin.env.security.user.userFromCall
import com.kcrud.core.admin.settings.AppSettings
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

/**
 * Configures logging within the application using CallLogging and CallId plugins.
 *
 * CallLogging is responsible for logging details about incoming HTTP requests and responses,
 * which includes URL, headers, response status, and more. It can be tailored to log specific
 * request attributes and supports including the call ID in logs.
 *
 * CallId generates or retrieves a unique identifier for each request, facilitating
 * end-to-end request tracing and correlation in logs, essential for debugging and monitoring.
 *
 * See: [CallLogging Documentation](https://ktor.io/docs/call-logging.html)
 *
 * See: [CallId Documentation](https://ktor.io/docs/call-id.html)
 */
fun Application.configureCallLogging() {

    // Set the machine ID used for generating Snowflake IDs.
    SnowflakeFactory.setMachineId(id = AppSettings.server.machineId)

    install(CallLogging) {
        level = Level.INFO

        // Integrates the unique call ID into the Mapped Diagnostic Context (MDC) for logging.
        // This allows the call ID to be included in each log entry, linking logs to specific requests.
        callIdMdc(name = "id")

        // Add the request duration to the log message.
        format { call ->
            // An example using a custom extension function to retrieve the user from the current
            // call's principal and print it in the metrics log message.
            // Usually we would not do this. Security must be enabled to be able to retrieve the
            // user from the call. Otherwise, will be retrieved only when creating a new token.
            val user: ContextUser? = call.userFromCall()

            val callDurationMs = call.processingTimeMillis()
            "Call Metric: [${call.request.origin.remoteHost}] " +
                    "${call.request.httpMethod.value} - ${call.request.path()} " +
                    "- by '$user' - ${callDurationMs}ms"
        }
    }

    install(CallId) {
        // Generates a unique ID for each call. This ID is used for request tracing and logging.
        // Must be added to the logback.xml file to be included in logs. See %X{id} in logback.xml.
        generate {
            SnowflakeFactory.nextId()
        }

        // Optionally we can also include the IDs to the response headers,
        // so that it can be retrieved by the client for tracing.
        replyToHeader(headerName = HttpHeaders.XRequestId)
    }
}
