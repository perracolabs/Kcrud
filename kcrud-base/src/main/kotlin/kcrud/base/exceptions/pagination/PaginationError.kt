/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.exceptions.pagination

import io.ktor.http.*
import kcrud.base.exceptions.shared.BaseError

/**
 * Pagination concrete errors.
 */
sealed class PaginationError(
    status: HttpStatusCode,
    code: String,
    description: String
) : BaseError(status = status, code = code, description = description) {

    data object InvalidPageablePair : PaginationError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IPP",
        description = "Page attributes mismatch. Expected both 'page' and 'size', or none of them."
    )

    data object InvalidOrderPair : PaginationError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IOP",
        description = "Order attributes mismatch. Expected both 'order' and 'sort', or none of them."
    )

    data object InvalidOrderDirection : PaginationError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IOD",
        description = "Ordering sort direction is invalid."
    )

    companion object {
        private const val TAG: String = "PGN."
    }
}
