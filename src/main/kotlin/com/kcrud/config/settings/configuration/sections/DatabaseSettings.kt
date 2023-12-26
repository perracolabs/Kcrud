/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.configuration.sections

import com.kcrud.data.database.DatabaseManager

/**
 * Database related settings.
 *
 * @property mode The database mode, either in-memory or persistent.
 * @property dbType The target database type.
 * @property name The name of the database.
 * @property path The database file location.
 * @property jdbcUrl The JDBC url database connection.
 * @property jdbcDriver The JDBC driver class name.
 * @property connectionPoolSize The database connection pool size. 0 for no connection pooling.
 */
internal data class DatabaseSettings(
    val mode: DatabaseManager.Mode,
    val dbType: DatabaseManager.DBType,
    val name: String,
    val path: String,
    val jdbcUrl: String,
    val jdbcDriver: String,
    val connectionPoolSize: Int
)
