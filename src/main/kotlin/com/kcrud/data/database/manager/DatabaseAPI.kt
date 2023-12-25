/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.data.database.manager

/**
 * Annotation for controlled access to the Database Management code,
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used within Database Management contexts.")
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseAPI