/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.exceptions.pagination

import kcrud.base.exceptions.shared.BaseException

/**
 * Represents pagination related exceptions.
 *
 * @param error The specific [PaginationError] enumeration representing the error that occurred.
 * @param reason An optional human-readable reason or additional context about the exception.
 */
class PaginationException(
    error: PaginationError,
    reason: String? = null
) : BaseException(error = error, reason = reason)
