/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.config.sections.security

import com.kcrud.config.settings.config.sections.SecuritySettings

/**
 * Encryption key settings.
 *
 * @property algorithm Algorithm used for encrypting/decrypting data.
 * @property salt Salt used for encrypting/decrypting data.
 * @property key Secret key for encrypting/decrypting data.
 */
internal data class EncryptionSettings(
    val algorithm: String,
    val salt: String,
    val key: String
) {
    init {
        require(algorithm.isNotBlank()) { "Missing encryption algorithm." }
        require(salt.isNotBlank()) { "Missing encryption salt." }
        require(key.isNotBlank() && (key.length >= SecuritySettings.MIN_KEY_LENGTH)) {
            "Invalid encryption key. Must be >= ${SecuritySettings.MIN_KEY_LENGTH} characters long."
        }
    }
}
