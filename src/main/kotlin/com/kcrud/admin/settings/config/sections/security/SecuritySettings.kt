/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.admin.settings.config.sections.security

import com.kcrud.admin.settings.config.sections.security.sections.BasicAuthSettings
import com.kcrud.admin.settings.config.sections.security.sections.ConstraintsSettings
import com.kcrud.admin.settings.config.sections.security.sections.EncryptionSettings
import com.kcrud.admin.settings.config.sections.security.sections.JwtSettings

/**
 * Top level section for the Security related settings.
 *
 * @property encryption Settings related to encryption, such as the encryption keys.
 * @property constraints Settings related to security constraints, such endpoints rate limits.
 * @property jwt Settings related to JWT authentication, such as the JWT secrets.
 * @property basic Settings related to basic authentication, such as the basic auth credentials.
 */
internal data class SecuritySettings(
    val encryption: EncryptionSettings,
    val constraints: ConstraintsSettings,
    val jwt: JwtSettings,
    val basic: BasicAuthSettings
) {
    companion object {
        const val MIN_USERNAME_LENGTH: Int = 4
        const val MIN_KEY_LENGTH: Int = 12
    }
}
