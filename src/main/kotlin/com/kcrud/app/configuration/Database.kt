package com.kcrud.app.configuration

import com.kcrud.data.models.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Provides functionality to initialize and manage a connection to the database.
 */
object DatabaseFactory {

    enum class Mode {
        /** Represents an in-memory database mode. */
        IN_MEMORY,

        /** Represents a file-based (persistent) database mode. */
        PERSISTENT
    }

    private const val DRIVER_CLASS_NAME = "org.h2.Driver"
    private const val DATABASE_NAME = "dbv1"

    /**
     * Initializes the database connection based on the provided mode.
     *
     * @param mode The mode in which the database should be initialized.
     */
    fun init(mode: Mode) {
        val jdbcURL = getJdbcUrl(mode)
        val database = Database.connect(url = jdbcURL, driver = DRIVER_CLASS_NAME)

        transaction(database) {
            SchemaUtils.create(UserTable)
            println("Database ready.")
        }
    }

    /**
     * Returns the JDBC URL corresponding to the provided database mode.
     *
     * @param mode The mode for which the JDBC URL is needed.
     * @return The JDBC URL for the specified mode.
     */
    private fun getJdbcUrl(mode: Mode): String {
        return when (mode) {
            Mode.IN_MEMORY -> "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
            Mode.PERSISTENT -> "jdbc:h2:file:./build/$DATABASE_NAME"
        }
    }
}
