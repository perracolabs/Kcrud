/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system.healthcheck.checks

import com.kcrud.settings.AppSettings
import com.zaxxer.hikari.HikariDataSource
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name

@Suppress("unused")
@Serializable
data class DatabaseCheck(
    val errors: MutableList<String> = mutableListOf(),
    val alive: Boolean,
    val datasource: Datasource? = null,
    val connectionTest: ConnectionTest? = null,
    val configuration: Configuration = Configuration()
) {
    init {
        val className = this::class.simpleName

        if (!alive) {
            errors.add("$className. Database is not responding.")
        }

        connectionTest?.let {
            if (!it.established) {
                errors.add("$className. Database connection not established.")
            }

            if (it.isReadOnly) {
                errors.add("$className. Database connection is read-only.")
            }
        } ?: errors.add("$className. Unable to test database connection.")
    }

    @Serializable
    data class ConnectionTest(
        val established: Boolean,
        val isReadOnly: Boolean,
        val name: String,
        val version: String,
        val dialect: String,
        val url: String,
        val vendor: String,
        val autoCommit: Boolean,
        val catalog: String,
    ) {
        companion object {
            fun build(database: Database?): ConnectionTest? {
                return database?.let {
                    runCatching {
                        val connector = it.connector()
                        try {
                            return ConnectionTest(
                                established = !connector.isClosed,
                                isReadOnly = connector.readOnly,
                                name = it.name,
                                version = it.version.toString(),
                                dialect = it.dialect.name,
                                url = it.url,
                                vendor = it.vendor,
                                autoCommit = connector.autoCommit,
                                catalog = connector.catalog
                            )
                        } finally {
                            connector.close()
                        }
                    }.getOrNull()
                }
            }
        }
    }

    @Serializable
    data class Datasource(
        val isPoolRunning: Boolean,
        val totalConnections: Int,
        val activeConnections: Int,
        val idleConnections: Int,
        val threadsAwaitingConnection: Int,
        val connectionTimeout: Long,
        val maxLifetime: Long,
        val keepaliveTime: Long,
        val maxPoolSize: Int
    ) {
        companion object {
            fun build(datasource: HikariDataSource?): Datasource? {
                return datasource?.let {
                    Datasource(
                        isPoolRunning = it.isRunning,
                        totalConnections = it.hikariPoolMXBean?.totalConnections ?: 0,
                        activeConnections = it.hikariPoolMXBean?.activeConnections ?: 0,
                        idleConnections = it.hikariPoolMXBean?.idleConnections ?: 0,
                        threadsAwaitingConnection = it.hikariPoolMXBean?.threadsAwaitingConnection ?: 0,
                        connectionTimeout = it.connectionTimeout,
                        maxLifetime = it.maxLifetime,
                        keepaliveTime = it.keepaliveTime,
                        maxPoolSize = it.maximumPoolSize
                    )
                }
            }
        }
    }

    @Serializable
    data class Configuration(
        val poolSize: Int = AppSettings.database.connectionPoolSize,
        val jdbcDriver: String = AppSettings.database.jdbcDriver,
        val jdbcUrl: String = AppSettings.database.jdbcUrl,
    )
}
