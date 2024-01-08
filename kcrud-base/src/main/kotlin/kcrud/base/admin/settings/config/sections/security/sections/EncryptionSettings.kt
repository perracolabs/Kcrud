/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.settings.config.sections.security.sections

import kcrud.base.admin.settings.config.parser.ConfigSection
import kcrud.base.admin.settings.config.sections.security.SecuritySettings

/**
 * Encryption key settings.
 *
 * @property algorithm Algorithm used for encrypting/decrypting data.
 * @property salt Salt used for encrypting/decrypting data.
 * @property key Secret key for encrypting/decrypting data.
 */
data class EncryptionSettings(
    val algorithm: String,
    val salt: String,
    val key: String
) : ConfigSection {
    init {
        require(algorithm.isNotBlank()) { "Missing encryption algorithm." }
        require(salt.isNotBlank()) { "Missing encryption salt." }
        require(key.isNotBlank() && (key.length >= SecuritySettings.MIN_KEY_LENGTH)) {
            "Invalid encryption key. Must be >= ${SecuritySettings.MIN_KEY_LENGTH} characters long."
        }
    }
}
