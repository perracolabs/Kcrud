/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.shared

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.data.database.tables.EmployeeTable
import com.kcrud.data.database.tables.EmploymentTable
import com.kcrud.utils.Tracer
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
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
internal object DatabaseManager {
    private val tracer = Tracer.create<DatabaseManager>()

    @Suppress("unused")
    enum class Mode {
        /** Represents an in-memory database mode. */
        IN_MEMORY,

        /** Represents a file-based (persistent) database mode. */
        PERSISTENT
    }

    @Suppress("unused")
    enum class DBType(val driver: String) {
        H2(driver = "org.h2.Driver"),
        SQLITE(driver = "org.sqlite.JDBC")
    }

    /**
     * Initializes the database connection based on the provided mode and database type.
     *
     * @param createSchema Whether to create the database schema if such does not exist.
     */
    @OptIn(DatabaseAPI::class)
    fun init(createSchema: Boolean = true) {
        val connectionDetails: ConnectionDetails = ConnectionDetails.build()

        setDatabaseHooks(connectionDetails)

        // Establish the database connection using Database.connect from the 'Exposed' framework.
        // This method not only establishes a connection but also registers it in a global registry within Exposed.
        // As a result, this connection becomes available application-wide without the need for a global variable.
        // Exposed manages this connection, utilizing a connection pool for efficiency and performance.
        // The connection pool reuses connections where possible and handles their lifecycle,
        // facilitating implicit and consistent database access throughout the application.

        val database: Database = if (connectionDetails.connectionPoolSize > 0) {
            val dataSource = createHikariDataSource(
                poolSize = connectionDetails.connectionPoolSize,
                url = connectionDetails.jdbcUrl,
                driver = connectionDetails.driver
            )
            Database.connect(datasource = dataSource)
        } else {
            Database.connect(url = connectionDetails.jdbcUrl, driver = connectionDetails.driver)
        }

        if (createSchema) {
            tracer.info("Setting database schema.")
            setupDatabase(database)
        }

        tracer.info("Database ready: $connectionDetails.")
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

    @OptIn(DatabaseAPI::class)
    private fun setDatabaseHooks(connectionDetails: ConnectionDetails) {
        if (connectionDetails.mode == Mode.IN_MEMORY && connectionDetails.dbType == DBType.SQLITE) {
            // In-memory sqlite databases get destroyed between transactions.
            // Getting a connection will preserve the in-memory database unless explicitly closed.
            // See: https://github.com/JetBrains/Exposed/issues/726#issuecomment-932202379
            val connection = DriverManager.getConnection(connectionDetails.jdbcUrl)

            // Add shutdown hook to close the connection.
            // This is not really needed for in-memory databases. Added just as a simple show-how exercise.
            Runtime.getRuntime().addShutdownHook(Thread {
                connection?.let {
                    if (!it.isClosed) {
                        tracer.info("Shutdown hook triggered. Closing database connection.")
                        it.close()
                        tracer.info("Database connection closed.")
                    }
                }
            })
        }
    }

    /**
     * Create a HikariDataSource to enable database connection pooling.
     *
     * See: [Database Pooling](https://ktor.io/docs/connection-pooling-caching.html#connection-pooling)
     */
    private fun createHikariDataSource(poolSize: Int, url: String, driver: String): HikariDataSource {
        require(poolSize > 0) { "Database connection pooling must be at >= 1." }

        return HikariDataSource(HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            maximumPoolSize = poolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }
}
