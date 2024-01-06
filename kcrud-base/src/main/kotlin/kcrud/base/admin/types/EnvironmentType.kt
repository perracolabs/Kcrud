/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.types

/**
 * The supported deployment types.
 */
enum class EnvironmentType {
    /** Development environment. */
    DEV,

    /** Test environment. */
    TEST,

    /** Production environment. */
    PROD
}
