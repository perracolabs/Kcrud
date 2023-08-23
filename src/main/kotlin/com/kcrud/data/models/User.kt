package com.kcrud.data.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table


internal object UserTable : Table(name = "user") {
    val id = integer(name = "user_id").autoIncrement()
    val name = varchar(name = "name", length = 64)
    val age = integer(name = "age")

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class UserEntity(
    val id: Int? = null,
    val name: String,
    val age: Int
)

@Serializable
data class UserPatchDTO(
    val name: String? = null,
    val age: Int? = null
)
