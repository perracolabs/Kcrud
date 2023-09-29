/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database

import com.kcrud.data.models.EmployeeTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.DriverManager

/**
 * Provides functionality to initialize and manage a connection to the database.
 *
 * H2 and SQLite are supported, for both in-memory and file based.
 */
object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    enum class Mode {
        /** Represents an in-memory database mode. */
        IN_MEMORY,

        /** Represents a file-based (persistent) database mode. */
        PERSISTENT
    }

    enum class DBType {
        H2,
        SQLite
    }

    // Prefix for the database name.
    private const val DB_NAME = "dbv1"

    // The path for persistent database storage.
    private const val DB_PATH = "./.database/"

    /**
     * Initializes the database connection based on the provided mode and database type.
     *
     * @param mode The mode in which the database should be initialized.
     * @param type The type of the database to use.
     */
    fun init(mode: Mode, type: DBType) {
        val connectionDetails = getConnectionDetails(mode, type)

        if (mode == Mode.IN_MEMORY && type == DBType.SQLite) {
            // In-memory sqlite databases get destroyed between transactions.
            // Getting a connection will preserve the in-memory database unless explicitly closed.
            // See: https://github.com/JetBrains/Exposed/issues/726#issuecomment-932202379
            val connection = DriverManager.getConnection(connectionDetails.jdbcUrl)

            // Add shutdown hook to close the connection.
            // This is not really needed for in-memory databases. Added just as a simple show-how exercise.
            Runtime.getRuntime().addShutdownHook(Thread {
                connection?.let {
                    if (!it.isClosed) {
                        logger.info("Shutdown hook triggered. Closing database connection.")
                        it.close()
                        logger.info("Database connection closed.")
                    }
                }
            })
        }

        val database = Database.connect(url = connectionDetails.jdbcUrl, driver = connectionDetails.driver)

        transaction(database) {
            SchemaUtils.create(EmployeeTable)
            logger.info("Database ready.")
        }
    }

    /**
     * Returns the JDBC URL and Driver class name corresponding to the provided database mode and type.
     *
     * @param mode The mode for which the JDBC URL is needed.
     * @param type The type of the database to use.
     * @return The resolved JDBC URL and Driver class name.
     */
    private fun getConnectionDetails(mode: Mode, type: DBType): ConnectionDetails {
        val path = "$DB_PATH${type.name}"
        Files.createDirectories(Paths.get(path))

        val dbName = "$path/$DB_NAME"

        return when (type) {
            DBType.H2 -> when (mode) {
                Mode.IN_MEMORY -> ConnectionDetails(jdbcUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
                Mode.PERSISTENT -> ConnectionDetails(jdbcUrl = "jdbc:h2:file:$dbName", driver = "org.h2.Driver")
            }

            DBType.SQLite -> when (mode) {
                Mode.IN_MEMORY -> ConnectionDetails(jdbcUrl = "jdbc:sqlite:file:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
                Mode.PERSISTENT -> ConnectionDetails(jdbcUrl = "jdbc:sqlite:$dbName.db", driver = "org.sqlite.JDBC")
            }
        }
    }

    private data class ConnectionDetails(val jdbcUrl: String, val driver: String)
}
