/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.shared

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.database.tables.EmploymentTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.DriverManager

/**
 * Manages database configurations and provides utility methods for database operations,
 * serving as a centralized point for setting up database connections, transactions,
 * and other database-related configurations.
 *
 * Example usage:
 * ```
 * DatabaseManager.init(Mode.PERSISTENT, DBType.H2)
 * ```
 */
object DatabaseManager {
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
     * @param mode The [Mode] in which the database should be initialized.
     * @param type The [DBType] defining the database to use.
     * @param createSchema Whether to create the database schema if such does not exist.
     */
    fun init(mode: Mode, type: DBType, createSchema: Boolean = true) {
        val connectionDetails = getConnectionDetails(mode, type)

        setDatabaseHooks(mode, type, connectionDetails)

        // Establish the database connection using Database.connect from the 'Exposed' framework.
        // This method not only establishes a connection but also registers it in a global registry within Exposed.
        // As a result, this connection becomes available application-wide without the need for a global variable.
        // Exposed manages this connection, utilizing a connection pool for efficiency and performance.
        // The connection pool reuses connections where possible and handles their lifecycle,
        // facilitating implicit and consistent database access throughout the application.
        val database = Database.connect(url = connectionDetails.jdbcUrl, driver = connectionDetails.driver)

        if (createSchema) {
            logger.info("Setting database schema.")
            setupDatabase(database)
        }

        logger.info("Database ready.")
    }

    /**
     * Creates the database schema if such does not exist.
     *
     * Database migrations are not supported, so altering the database tables
     * will make this method fail if the database has been previously crated.
     *
     * For migrations should use external libraries such as Flyway or Liquibase.
     */
    private fun setupDatabase(database: Database) {
        transaction(database) {
            SchemaUtils.create(
                ContactTable,
                EmployeeTable,
                EmploymentTable
            )
        }
    }

    private fun setDatabaseHooks(mode: Mode, type: DBType, connectionDetails: ConnectionDetails) {
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
    }

    /**
     * Returns the JDBC URL and Driver class name corresponding to the provided database mode and type.
     *
     * Ideally all these settings would be read from a configuration file.
     */
    private fun getConnectionDetails(mode: Mode, type: DBType): ConnectionDetails {
        val path = "$DB_PATH${type.name}"
        Files.createDirectories(Paths.get(path))

        val dbName = "$path/$DB_NAME"

        return when (type) {
            DBType.H2 -> when (mode) {
                Mode.IN_MEMORY -> ConnectionDetails(
                    jdbcUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;",
                    driver = "org.h2.Driver"
                )

                Mode.PERSISTENT -> ConnectionDetails(
                    jdbcUrl = "jdbc:h2:file:$dbName",
                    driver = "org.h2.Driver",
                )
            }

            DBType.SQLite -> when (mode) {
                Mode.IN_MEMORY -> ConnectionDetails(
                    jdbcUrl = "jdbc:sqlite:file:test?mode=memory&cache=shared",
                    driver = "org.sqlite.JDBC"
                )

                Mode.PERSISTENT -> ConnectionDetails(
                    jdbcUrl = "jdbc:sqlite:$dbName.db",
                    driver = "org.sqlite.JDBC"
                )
            }
        }
    }

    private data class ConnectionDetails(val jdbcUrl: String, val driver: String)
}

