/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.admin.settings.config.sections.security.sections

import com.kcrud.core.admin.settings.config.sections.security.SecuritySettings.Companion.MIN_KEY_LENGTH

/**
 * JWT authentication settings.
 *
 * @property isEnabled Flag to enable/disable JWT authentication.
 * @property tokenLifetime Authentication token lifetime, in milliseconds.
 * @property audience Intended recipients of the JWT.
 * @property issuer Provider that issues the JWT.
 * @property realm Security realm for the JWT authentication.
 * @property secretKey Secret key for signing the JWT.
 */
internal data class JwtSettings(
    val isEnabled: Boolean,
    val tokenLifetime: Long,
    val audience: String,
    val issuer: String,
    val realm: String,
    val secretKey: String
) {
    init {
        require(tokenLifetime > 0) { "Invalid JWT token lifetime. Must be > 0." }
        require(audience.isNotBlank()) { "Missing JWT audience." }
        require(issuer.isNotBlank()) { "Missing JWT issuer." }
        require(realm.isNotBlank()) { "Missing JWT realm." }
        require(secretKey.isNotBlank() && (secretKey.length >= MIN_KEY_LENGTH)) {
            "Invalid JWT secret key. Must be >= $MIN_KEY_LENGTH characters long."
        }
    }
}
