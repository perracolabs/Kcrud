/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.shared

import java.nio.file.Files
import java.nio.file.Paths


/**
 * Provides the JDBC URL and Driver class name for the database.
 */
@DatabaseAPI
internal object ConnectionDetails {

    data class Profile(val jdbcUrl: String, val driver: String)

    // Prefix for the database name.
    private const val DB_NAME = "dbv1"

    // The path for persistent database storage.
    private const val DB_PATH = "./.database/"

    /**
     * Returns the JDBC URL and Driver class name corresponding to the provided database mode and type.
     *
     * Ideally all these settings would be read from a configuration file.
     */
    fun get(mode: DatabaseManager.Mode, type: DatabaseManager.DBType): Profile {
        val path = "$DB_PATH${type.name}"
        Files.createDirectories(Paths.get(path))

        val dbName = "$path/${DB_NAME}"

        return when (type) {
            DatabaseManager.DBType.H2 -> when (mode) {
                DatabaseManager.Mode.IN_MEMORY -> Profile(
                    jdbcUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
                    driver = "org.h2.Driver"
                )

                DatabaseManager.Mode.PERSISTENT -> Profile(
                    jdbcUrl = "jdbc:h2:file:$dbName",
                    driver = "org.h2.Driver",
                )
            }

            DatabaseManager.DBType.SQLite -> when (mode) {
                DatabaseManager.Mode.IN_MEMORY -> Profile(
                    jdbcUrl = "jdbc:sqlite:file:test?mode=memory&cache=shared",
                    driver = "org.sqlite.JDBC"
                )

                DatabaseManager.Mode.PERSISTENT -> Profile(
                    jdbcUrl = "jdbc:sqlite:$dbName.db",
                    driver = "org.sqlite.JDBC"
                )
            }
        }
    }
}