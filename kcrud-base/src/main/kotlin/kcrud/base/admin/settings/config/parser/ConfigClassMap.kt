/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.settings.config.parser

import kcrud.base.admin.settings.annotation.ConfigurationAPI
import kotlin.reflect.KClass

/**
 * Maps a configuration path to a data class type.
 *
 * @param path The configuration path.
 * @param argument The constructor argument name for the data class.
 * @param kClass The target data class type to be instantiated.
 * @return A [ConfigClassMap] object containing the mapping.
 */
@ConfigurationAPI
data class ConfigClassMap(val path: String, val argument: String, val kClass: KClass<*>)
