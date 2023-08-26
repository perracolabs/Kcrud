package com.kcrud.data.models
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date


internal object EmployeeTable : Table(name = "employee") {
    val id = integer(name = "employee_id").autoIncrement()
    val firstName = varchar(name = "first_name", length = 64)
    val lastName = varchar(name = "last_name", length = 64)
    val dob = date(name = "dob")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class EmployeeEntity(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate
)

@Serializable
data class EmployeePatchDTO(
    val firstName: String? = null,
    val lastName: String? = null,
    val dob: LocalDate? = null
)
