/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.kcrud.data.database.tables.ContactTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

data class Contact(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: UUID? = null,
    val email: String,
    val phone: String
) {
    init {
        require(email.isNotBlank()) { "Email can't be empty." }
        require(phone.isNotBlank()) { "Phone can't be empty." }
    }

    companion object {
        fun fromTableRow(row: ResultRow): Contact {
            return Contact(
                id = row[ContactTable.id],
                email = row[ContactTable.email],
                phone = row[ContactTable.phone]
            )
        }
    }
}
