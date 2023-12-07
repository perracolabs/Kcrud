/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.tables

import org.jetbrains.exposed.sql.Table

/**
 * Database entity for employee contact details.
 */
internal object ContactTable : Table(name = "contact") {
    val id = uuid(name = "contact_id").autoGenerate()
    val email = varchar(name = "email", length = 128)
    val phone = varchar(name = "phone", length = 16)

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Contact_ID")
}
