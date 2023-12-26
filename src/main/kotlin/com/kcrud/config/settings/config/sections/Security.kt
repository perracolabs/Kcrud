/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.config.sections

import com.kcrud.config.settings.config.sections.security.BasicAuth
import com.kcrud.config.settings.config.sections.security.Constraints
import com.kcrud.config.settings.config.sections.security.Encryption
import com.kcrud.config.settings.config.sections.security.Jwt

/**
 * Security class holds settings related to security.
 *
 * @property encryption Settings related to encryption.
 * @property constraints Settings related to security constraints.
 * @property jwt Settings related to JWT authentication.
 * @property basicAuth Settings related to basic authentication.
 */
internal data class Security(
    val encryption: Encryption,
    val constraints: Constraints,
    val jwt: Jwt,
    val basicAuth: BasicAuth
) {
    companion object {
        const val MIN_USERNAME_LENGTH: Int = 4
        const val MIN_KEY_LENGTH: Int = 12
    }
}
