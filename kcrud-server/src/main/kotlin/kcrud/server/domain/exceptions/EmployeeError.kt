/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.server.domain.exceptions

import io.ktor.http.*
import kcrud.base.exceptions.shared.BaseError
import kcrud.base.exceptions.shared.ErrorCodeRegistry
import java.util.*

sealed class EmployeeError(
    status: HttpStatusCode,
    code: String,
    description: String
) : BaseError(status = status, code = code, description = description) {

    data class EmployeeNotFound(val id: UUID) : EmployeeError(
        status = HttpStatusCode.NotFound,
        code = "${TAG}ENF",
        description = "Employee not found. Id: $id"
    )

    companion object {
        private const val TAG: String = "EMP."

        init {
            ErrorCodeRegistry.registerTag(tag = TAG)
        }
    }
}
