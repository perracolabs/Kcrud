/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction

/**
 * A simple tracer wrapper to provide a consistent logging interface.
 */
@Suppress("unused")
class Tracer(private val logger: Logger) {

    fun trace(message: String) {
        logger.trace(message)
    }

    fun debug(message: String) {
        logger.debug(message)
    }

    fun info(message: String) {
        logger.info(message)
    }

    fun warning(message: String) {
        logger.warn(message)
    }

    fun error(message: String) {
        logger.error(message)
    }

    fun error(message: String, throwable: Throwable) {
        logger.error(message, throwable)
    }

    companion object {
        /**
         * Whether to show the full package name.
         * If false, only the class name will be shown.
         */
        const val LOG_FULL_PACKAGE = true

        /**
         * Creates a new [Tracer] instance for the given class.
         * Used at level class contexts.
         */
        inline operator fun <reified T : Any> invoke(): Tracer {
            val loggerName = if (LOG_FULL_PACKAGE) {
                T::class.qualifiedName ?: T::class.java.simpleName
            } else {
                T::class.java.simpleName
            }

            return Tracer(LoggerFactory.getLogger(loggerName))
        }

        /**
         * Creates a new [Tracer] instance for the given tag.
         * Used at top-level functions or non-class contexts.
         */
        fun byTag(tag: String): Tracer {
            return Tracer(LoggerFactory.getLogger(tag))
        }

        /**
         * Combines a class name (T) and function name into a single string.
         *
         * When used in extension functions, "T" should be the receiver type
         * of the extension function.
         *
         * Usage Example with an Extension Function:
         * ```
         * fun String.yourExtensionFunction() {
         *     val tracerTag = ::yourExtensionFunction.nameWithClass<String>()
         *     Tracer.create(tracerTag).info("Some log message.")
         * }
         * ```
         *
         * @param T The class containing the function or the receiver type for extension functions.
         * @return String representing the full name of the function including its class.
         */
        inline fun <reified T : Any> KFunction<*>.nameWithClass(): String {
            val loggerName = if (LOG_FULL_PACKAGE) {
                T::class.qualifiedName ?: T::class.java.simpleName
            } else {
                T::class.java.simpleName
            }

            return "$loggerName.${this.name}"
        }
    }
}
