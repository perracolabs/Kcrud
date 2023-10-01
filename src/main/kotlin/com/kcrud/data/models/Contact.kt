/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models

import com.kcrud.data.database.entities.Contacts
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Contact(
    var id: Int? = null,
    val email: String,
    val phone: String
) {
    init {
        require(email.isNotBlank()) { "Email can't be empty." }
        require(email.isNotBlank()) { "Phone can't be empty." }
    }

    companion object {
        fun fromTableRow(row: ResultRow): Contact {
            return Contact(
                id = row[Contacts.id],
                email = row[Contacts.email],
                phone = row[Contacts.phone]
            )
        }
    }
}
