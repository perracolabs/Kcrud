package com.kcrud.data.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


internal object EmployeeTable : Table(name = "employee") {
    val id = integer(name = "employee_id").autoIncrement()
    val name = varchar(name = "name", length = 64)
    val age = integer(name = "age")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class EmployeeEntity(
    val id: Int? = null,
    val name: String,
    val age: Int
)

@Serializable
data class EmployeePatchDTO(
    val name: String? = null,
    val age: Int? = null
)
