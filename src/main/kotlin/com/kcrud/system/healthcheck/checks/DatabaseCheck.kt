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
    val database: Details? = null,
    val datasource: Datasource? = null,
    val configuration: Configuration = Configuration()
) {
    init {
        val className = this::class.simpleName

        if (!alive) {
            errors.add("$className. Database is not responding.")
        }

        database?.let {
            if (it.isClosed) {
                errors.add("$className. Database is closed.")
            }

            if (it.isReadOnly) {
                errors.add("$className. Database is read-only.")
            }
        } ?: errors.add("$className. Database not set.")
    }

    @Serializable
    data class Details(
        val isClosed: Boolean,
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
            fun build(database: Database?): Details? {
                return database?.let {
                    val connector = it.connector()
                    Details(
                        isClosed = connector.isClosed,
                        isReadOnly = connector.readOnly,
                        name = it.name,
                        version = it.version.toString(),
                        dialect = it.dialect.name,
                        url = it.url,
                        vendor = it.vendor,
                        autoCommit = connector.autoCommit,
                        catalog = connector.catalog
                    ).also {
                        connector.close()
                    }
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
