/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.core.admin.env.healthcheck.annotation

/**
 * Annotation for controlled access to the Health Check API.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used within the Health Check API.")
@Retention(AnnotationRetention.BINARY)
annotation class HealthCheckAPI
