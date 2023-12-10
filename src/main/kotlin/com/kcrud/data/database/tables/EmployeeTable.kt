/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.tables

import com.kcrud.data.models.employee.MaritalStatus
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
    val maritalStatus = enumeration("marital_status", MaritalStatus::class)
    val contactId = uuid(name = "contact_id") references ContactTable.id

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Employee_ID")
}
