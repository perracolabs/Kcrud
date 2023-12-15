/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.settings

/**
 * Annotation designed for controlled access to the Settings management code,
 * offering a workaround for the lack of package-private visibility in Kotlin.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used in Settings contexts.")
@Retention(AnnotationRetention.BINARY)
annotation class SettingsAPI
