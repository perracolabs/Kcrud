/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.data.utils

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * Example in which each item has an id,
 * which is the actual value that will be stored in the database,
 * instead of the name of the enum item.
 */
interface EnumWithId {
    val id: Int
}

/**
 * Creates a column in an Exposed Table to store enum values by their integer IDs,
 * simplifying the process of defining a column in an Exposed table that corresponds
 * to an enum type, where the enum values are stored in the database as integers.
 * It uses a custom enumeration column type in Exposed to map between the enum values
 * in Kotlin and their integer IDs in the database.
 *
 * @param T The enum class type. This class must implement the EnumWithId interface.
 * @param name The name of the column in the database.
 * @param fromId A function that takes an integer ID and returns the corresponding enum value.
 *               It should return null if the ID does not correspond to any enum value.
 * @return A Column of type T representing the enum in the Exposed table.
 * @throws IllegalArgumentException if an unknown enum id is encountered in the database.
 *
 * Usage Example:
 * ```
 * enum class Honorific(override val id: Int) : EnumWithId {
 *     MR(100), MRS(101), ...
 *     companion object {
 *         private val map = Honorific.entries.associateBy(Honorific::id)
 *         fun fromId(id: Int) = map[id]
 *     }
 * }
 *
 * object EmployeeTable : Table() {
 *     val honorific = enumById("honorific", Honorific::fromId)
 *     ...
 * }
 * ```
 */
inline fun <reified T : Enum<T>> Table.enumById(
    name: String,
    noinline fromId: (Int) -> T?
): Column<T> {
    return customEnumeration(
        name = name,
        sql = "INTEGER",
        fromDb = { value -> fromId(value as Int) ?: throw IllegalArgumentException("Unknown enum id: $value") },
        toDb = { (it as EnumWithId).id }
    )
}
