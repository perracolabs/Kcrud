/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.data.utils

import kcrud.core.admin.settings.AppSettings
import org.jetbrains.exposed.crypt.Algorithms
import org.jetbrains.exposed.crypt.Encryptor

/**
 * Utility class for database field encryption.
 */
object EncryptionUtils {
    private enum class AlgorithmName {
        AES_256_PBE_CBC,
        AES_256_PBE_GCM,
        BLOW_FISH,
        TRIPLE_DES
    }

    fun getEncryptor(): Encryptor {
        val encryption = AppSettings.security.encryption
        val algorithm: AlgorithmName = AlgorithmName.valueOf(encryption.algorithm)

        return when (algorithm) {
            AlgorithmName.AES_256_PBE_CBC -> Algorithms.AES_256_PBE_CBC(password = encryption.key, salt = encryption.salt)
            AlgorithmName.AES_256_PBE_GCM -> Algorithms.AES_256_PBE_GCM(password = encryption.key, salt = encryption.salt)
            AlgorithmName.BLOW_FISH -> Algorithms.BLOW_FISH(key = encryption.key)
            AlgorithmName.TRIPLE_DES -> Algorithms.TRIPLE_DES(secretKey = encryption.key)
        }
    }
}
