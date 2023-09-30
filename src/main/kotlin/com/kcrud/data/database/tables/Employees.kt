/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

/**
 * Database model for employees.
 */
internal object Employees : Table(name = "employee") {
    val id = integer(name = "employee_id").autoIncrement()
    val firstName = varchar(name = "first_name", length = 64)
    val lastName = varchar(name = "last_name", length = 64)
    val dob = date(name = "dob")
    val contactId = integer("contact_id") references Contacts.id

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Employee_ID")
}
