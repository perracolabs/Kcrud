/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database

import com.kcrud.config.env.healthcheck.checks.DatabaseCheck
import com.kcrud.data.tables.ContactTable
import com.kcrud.data.tables.EmployeeTable
import com.kcrud.data.tables.EmploymentTable
import com.kcrud.utils.Tracer
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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
    private val tracer = Tracer<DatabaseManager>()

    private var database: Database? = null
    private var hikariDataSource: HikariDataSource? = null

    @Suppress("unused")
    enum class Mode {
        /** Represents an in-memory database mode. */
        IN_MEMORY,

        /** Represents a file-based (persistent) database mode. */
        PERSISTENT
    }

    @Suppress("unused")
    enum class DBType {
        H2,
        SQLITE
    }

    /**
     * Initializes the database connection based on the provided mode and database type.
     *
     * @param createSchema Whether to create the database schema if such does not exist.
     */
    @OptIn(DatabaseAPI::class)
    fun init(createSchema: Boolean = true) {
        val connectionDetails: ConnectionDetails = ConnectionDetails.build()

        DatabaseUtils.setDatabaseHooks(connectionDetails = connectionDetails)

        // Establishes a database connection using the 'Exposed' framework.
        // If a connection pool size is specified, a HikariCP DataSource is configured to manage
        // the pool which optimizes resource usage by reusing connections and managing their lifecycle.
        // This setup enables application-wide database access without global variables.
        val databaseInstance: Database = if (connectionDetails.connectionPoolSize > 0) {
            val dataSource = DatabaseUtils.createHikariDataSource(
                poolSize = connectionDetails.connectionPoolSize,
                url = connectionDetails.jdbcUrl,
                driver = connectionDetails.jdbcDriver
            )
            hikariDataSource = dataSource
            Database.connect(datasource = dataSource)
        } else {
            Database.connect(url = connectionDetails.jdbcUrl, driver = connectionDetails.jdbcDriver)
        }

        if (createSchema) {
            tracer.info("Setting database schema.")
            setupDatabase(database = databaseInstance)
        }

        database = databaseInstance
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

    /**
     * Checks whether the database is alive.
     */
    private fun ping(): Boolean {
        return try {
            transaction {
                exec("SELECT 1;")
                true
            }
        } catch (e: Exception) {
            tracer.warning("Database is not alive.")
            false
        }
    }

    /**
     * Retrieves HikariCP health metrics.
     */
    fun getHealthCheck(): DatabaseCheck {
        return DatabaseCheck(
            alive = ping(),
            connectionTest = DatabaseCheck.ConnectionTest.build(database = database),
            datasource = DatabaseCheck.Datasource.build(datasource = hikariDataSource)
        )
    }
}
