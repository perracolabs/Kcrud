/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class ContactEntity(
    var id: Int? = null,
    val email: String,
    val phone: String
) {
    init {
        require(email.isNotBlank()) { "Email can't be empty." }
        require(email.isNotBlank()) { "Phone can't be empty." }
    }
}
