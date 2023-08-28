package com.kcrud.data.models

import kotlinx.serialization.Serializable


@Serializable
data class ContactDetailsEntity(
    var id: Int? = null,
    val email: String,
    val phone: String
) {
    init {
        require(email.isNotBlank()) { "Email can't be empty." }
        require(email.isNotBlank()) { "Phone can't be empty." }
    }
}
