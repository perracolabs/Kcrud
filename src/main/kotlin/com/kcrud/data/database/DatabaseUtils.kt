/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database

import com.kcrud.utils.Tracer
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.DriverManager

@DatabaseAPI
internal object DatabaseUtils {

    /**
     * Create a HikariDataSource to enable database connection pooling.
     *
     * See: [Database Pooling](https://ktor.io/docs/connection-pooling-caching.html#connection-pooling)
     */
    fun createHikariDataSource(poolSize: Int, url: String, driver: String): HikariDataSource {
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

    fun setDatabaseHooks(connectionDetails: ConnectionDetails) {
        if (connectionDetails.mode == DatabaseManager.Mode.IN_MEMORY && connectionDetails.dbType == DatabaseManager.DBType.SQLITE) {
            // In-memory sqlite databases get destroyed between transactions.
            // Getting a connection will preserve the in-memory database unless explicitly closed.
            // See: https://github.com/JetBrains/Exposed/issues/726#issuecomment-932202379
            val connection = DriverManager.getConnection(connectionDetails.jdbcUrl)

            // Add shutdown hook to close the connection.
            // This is not really needed for in-memory databases. Added just as a simple show-how exercise.
            Runtime.getRuntime().addShutdownHook(Thread {
                connection?.let {
                    if (!it.isClosed) {
                        val tracer = Tracer<DatabaseUtils>()
                        tracer.info("Shutdown hook triggered. Closing database connection.")
                        it.close()
                        tracer.info("Database connection closed.")
                    }
                }
            })
        }
    }
}
