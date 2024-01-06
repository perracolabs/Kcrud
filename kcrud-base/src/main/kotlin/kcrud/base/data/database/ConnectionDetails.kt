/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.data.database

import kcrud.base.admin.settings.config.sections.DatabaseSettings
import kcrud.base.data.database.annotation.DatabaseAPI
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Provides the JDBC URL and Driver class name for the database.
 */
@DatabaseAPI
internal data class ConnectionDetails(
    val jdbcUrl: String,
    val jdbcDriver: String,
    val mode: DatabaseFactory.Mode,
    val dbType: DatabaseFactory.DBType,
    val connectionPoolSize: Int
) {

    companion object {
        fun build(settings: DatabaseSettings): ConnectionDetails {
            val path = "${settings.path}${settings.dbType.name}"
            Files.createDirectories(Paths.get(path))

            return ConnectionDetails(
                jdbcUrl = settings.jdbcUrl,
                jdbcDriver = settings.jdbcDriver,
                mode = settings.mode,
                dbType = settings.dbType,
                connectionPoolSize = settings.connectionPoolSize
            )
        }
    }
}
