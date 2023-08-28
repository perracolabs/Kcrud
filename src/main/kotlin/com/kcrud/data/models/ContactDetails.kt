package com.kcrud.data.models

import org.jetbrains.exposed.sql.Table


/**
 * Database model for contact details.
 */
internal object ContactDetailsTable : Table(name = "contact_details") {
    val id = integer(name = "contact_details_id").autoIncrement()
    val email = varchar(name = "email", length = 128)
    val phone = varchar(name = "phone", length = 16)

    override val primaryKey = PrimaryKey(firstColumn = id, name = "PK_Contact_Details_ID")
}
