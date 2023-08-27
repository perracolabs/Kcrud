package com.kcrud.data.models

import com.kcrud.utils.AgeDelegate
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date


internal object EmployeeTable : Table(name = "employee") {
    val id = integer(name = "employee_id").autoIncrement()
    val firstName = varchar(name = "first_name", length = 64)
    val lastName = varchar(name = "last_name", length = 64)
    val dob = date(name = "dob")

    override val primaryKey = PrimaryKey(id, name = "PK_Employee_ID")
}

@Serializable
data class EmployeeEntity(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate
) {
    init {
        require(firstName.isNotEmpty()) { "First name can't be empty." }
        require(lastName.isNotEmpty()) { "Last name can't be empty." }
    }

    // Example of a simple dynamic field.
    val fullName: String = "$firstName $lastName"

    // Example of a dynamic field using a delegate.
    private val ageDelegate: Int by AgeDelegate(dob)
    val age = ageDelegate
}

@Serializable
data class EmployeePatchDTO(
    val firstName: String? = null,
    val lastName: String? = null,
    val dob: LocalDate? = null
)
