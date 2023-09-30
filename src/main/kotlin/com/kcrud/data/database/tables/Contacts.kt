/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.tables

import org.jetbrains.exposed.sql.Table

/**
 * Database model for employee contact details.
 */
internal object Contacts : Table(name = "contact") {
    val id = integer(name = "contact_id").autoIncrement()
    val email = varchar(name = "email", length = 128)
    val phone = varchar(name = "phone", length = 16)

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Contact_ID")
}
