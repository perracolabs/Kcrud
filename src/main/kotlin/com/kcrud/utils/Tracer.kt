/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.utils

import com.kcrud.config.env.types.DeploymentType
import com.kcrud.config.settings.AppSettings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

/**
 * A simple tracer wrapper to provide a consistent logging interface.
 */
@Suppress("unused")
internal class Tracer(private val logger: Logger) {

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

    /**
     * Logs a message at different severity levels based on the current configured [DeploymentType].
     * Intended for highlighting configurations or operations that might be allowed
     * for development or testing environments but not for production.
     * It logs the message as an error in production, as a warning in testing, and
     * as information in development environments.
     * This helps in quickly identifying potential misconfigurations or unintended
     * execution of certain code paths in specific deployment environments.
     *
     * @param message The message to log indicating the context or operation that needs attention.
     */
    fun byDeploymentType(message: String) {
        when (val deploymentType = AppSettings.deployment.type) {
            DeploymentType.PROD -> error("ATTENTION: '$deploymentType' environment >> $message")
            DeploymentType.TEST -> warning("ATTENTION: '$deploymentType' environment >> $message")
            DeploymentType.DEV -> info(message)
        }
    }

    companion object {
        /** Toggle for full package name or simple name. */
        const val LOG_FULL_PACKAGE = true

        /**
         * Creates a new [Tracer] instance for a given class.
         * Intended for classes where the class context is applicable.
         *
         * @param T The class for which the logger is being created.
         * @return Tracer instance with a logger named after the class.
         */
        inline operator fun <reified T : Any> invoke(): Tracer {
            val loggerName = if (LOG_FULL_PACKAGE) {
                T::class.qualifiedName ?: T::class.simpleName ?: "UnknownClass"
            } else {
                T::class.simpleName ?: "UnknownClass"
            }
            return Tracer(LoggerFactory.getLogger(loggerName))
        }

        /**
         * Creates a new [Tracer] instance for a given function.
         * Intended for top-level and extension functions where class context is not applicable.
         *
         * @param funcRef The reference to the function for which the logger is being created.
         * @return Tracer instance named after the function and its declaring class (if available).
         */
        fun <R> forFunction(funcRef: KFunction<R>): Tracer {
            val functionName = funcRef.name
            val declaringClass = funcRef.javaMethod?.declaringClass
            val className = when {
                (LOG_FULL_PACKAGE && (declaringClass != null)) -> declaringClass.name
                (declaringClass != null) -> declaringClass.simpleName
                else -> "UnknownClass"
            }
            val loggerName = "$className.$functionName"
            return Tracer(LoggerFactory.getLogger(loggerName))
        }
    }
}
