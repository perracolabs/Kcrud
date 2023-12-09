/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.models.contact

import com.kcrud.data.database.tables.ContactTable
import com.kcrud.utils.SUUID
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents the model for an employee's contact details.
 *
 * @property id The contact's id.
 * @property email The contact's email.
 * @property phone The contact's phone.
 */
@Serializable
data class Contact(
    var id: SUUID,
    val email: String,
    val phone: String
) {
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
