/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database

import com.kcrud.admin.settings.AppSettings
import java.nio.file.Files
import java.nio.file.Paths


/**
 * Provides the JDBC URL and Driver class name for the database.
 */
@DatabaseAPI
internal data class ConnectionDetails(
    val jdbcUrl: String,
    val jdbcDriver: String,
    val mode: DatabaseManager.Mode,
    val dbType: DatabaseManager.DBType,
    val connectionPoolSize: Int
) {

    companion object {
        fun build(): ConnectionDetails {
            val dbType = AppSettings.database.dbType

            val path = "${AppSettings.database.path}${dbType.name}"
            Files.createDirectories(Paths.get(path))

            return ConnectionDetails(
                jdbcUrl = AppSettings.database.jdbcUrl,
                jdbcDriver = AppSettings.database.jdbcDriver,
                mode = AppSettings.database.mode,
                dbType = AppSettings.database.dbType,
                connectionPoolSize = AppSettings.database.connectionPoolSize
            )
        }
    }
}
