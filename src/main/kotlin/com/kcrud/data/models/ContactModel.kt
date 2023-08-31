package com.kcrud.data.models

import org.jetbrains.exposed.sql.Table


/**
 * Database model for employee contact details.
 */
internal object ContactTable : Table(name = "contact") {
    val id = integer(name = "contact_id").autoIncrement()
    val email = varchar(name = "email", length = 128)
    val phone = varchar(name = "phone", length = 16)

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Contact_ID")
}