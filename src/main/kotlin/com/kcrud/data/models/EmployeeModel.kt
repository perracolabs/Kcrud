package com.kcrud.data.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date


/**
 * Database model for employees.
 */
internal object EmployeeTable : Table(name = "employee") {
    val id = integer(name = "employee_id").autoIncrement()
    val firstName = varchar(name = "first_name", length = 64)
    val lastName = varchar(name = "last_name", length = 64)
    val dob = date(name = "dob")
    val contactDetailsId = integer("contact_details_id") references ContactDetailsTable.id

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Employee_ID")
}
