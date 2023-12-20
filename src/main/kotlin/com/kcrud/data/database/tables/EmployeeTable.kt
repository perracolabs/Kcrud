/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.tables

import com.kcrud.data.models.employee.Honorific
import com.kcrud.data.models.employee.MaritalStatus
import com.kcrud.data.utils.enumById
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

/**
 * Database entity for employees.
 */
internal object EmployeeTable : Table(name = "employee") {
    val id = uuid(name = "employee_id").autoGenerate()
    val firstName = varchar(name = "first_name", length = 64)
    val lastName = varchar(name = "last_name", length = 64)
    val dob = date(name = "dob")

    // Example of an enum that is stored as a string in the database.
    val maritalStatus = enumerationByName(name = "marital_status", length = 64, MaritalStatus::class)
    // Example of an enum that is stored as an integer in the database.
    val honorific = enumById(name = "honorific", fromId = Honorific::fromId)

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Employee_ID")
}
