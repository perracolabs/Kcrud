/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.configuration.sections.security

import com.kcrud.config.settings.configuration.sections.security.sections.BasicAuthSettings
import com.kcrud.config.settings.configuration.sections.security.sections.ConstraintsSettings
import com.kcrud.config.settings.configuration.sections.security.sections.EncryptionSettings
import com.kcrud.config.settings.configuration.sections.security.sections.JwtSettings

/**
 * Security class holds settings related to security.
 *
 * @property encryption Settings related to encryption.
 * @property constraints Settings related to security constraints.
 * @property jwt Settings related to JWT authentication.
 * @property basicAuth Settings related to basic authentication.
 */
internal data class SecuritySettings(
    val encryption: EncryptionSettings,
    val constraints: ConstraintsSettings,
    val jwt: JwtSettings,
    val basicAuth: BasicAuthSettings
) {
    companion object {
        const val MIN_USERNAME_LENGTH: Int = 4
        const val MIN_KEY_LENGTH: Int = 12
    }
}
