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

sealed class EmploymentError(
    status: HttpStatusCode,
    code: String,
    description: String
) : BaseError(status = status, code = code, description = description) {

    data class EmploymentNotFound(val employeeId: UUID, val employmentId: UUID) : EmploymentError(
        status = HttpStatusCode.NotFound,
        code = "${TAG}ENF",
        description = "Employment not found. Employee Id: $employeeId. Employment Id: $employmentId"
    )

    companion object {
        private const val TAG: String = "EMT."

        init {
            ErrorCodeRegistry.registerTag(tag = TAG)
        }
    }
}
