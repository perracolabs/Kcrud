/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.settings.config.sections

import kcrud.base.data.database.DatabaseFactory

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
data class DatabaseSettings(
    val mode: DatabaseFactory.Mode,
    val dbType: DatabaseFactory.DBType,
    val name: String,
    val path: String,
    val jdbcUrl: String,
    val jdbcDriver: String,
    val connectionPoolSize: Int
)
