/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.exceptions.shared

import io.ktor.http.*

/**
 * Abstract base class for representing application concrete errors.
 * This class ensures that each error code is unique across all instances.
 *
 * @param status The [HttpStatusCode] associated with this error.
 * @param code A unique code identifying the type of error.
 * @param description A human-readable description of the error.
 */
abstract class BaseError(
    val code: String,
    val status: HttpStatusCode,
    val description: String
) {
    init {
        // Register the error code to ensure its uniqueness across all instances.
        registerCode(newCode = code)
    }

    companion object {
        private val registeredCodes = mutableSetOf<String>()

        /**
         * Registers a new error code and checks for uniqueness.
         * If the code is already registered, an exception is thrown.
         *
         * @param newCode The error code to be registered.
         * @throws IllegalArgumentException if the code is already registered.
         */
        fun registerCode(newCode: String) {
            require(newCode !in registeredCodes) { "Duplicate error code detected: $newCode" }
            registeredCodes += newCode
        }
    }
}
