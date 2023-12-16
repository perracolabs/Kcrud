/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.settings

import com.kcrud.graphql.GraphQLFramework
import io.ktor.server.config.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Creates an AppSettings instance by parsing the application configuration data.
 * This ensures strongly typed access to the configuration data throughout the application.
 */
@SettingsAPI
internal object SettingsParser {
    /**
     * Performs the configuration file parsing.
     *
     * @param config Configuration data parsed from the application's settings file.
     * @return A new AppSettings object populated with the parsed configuration data.
     */
    fun parse(config: ApplicationConfig): AppSettings {
        val global = instantiateConfig(config = config, keyPath = "ktor", kClass = AppSettings.Global::class)
        val deployment = instantiateConfig(config = config, keyPath = "ktor.deployment", kClass = AppSettings.Deployment::class)
        val graphql = instantiateConfig(config = config, keyPath = "ktor.graphql", kClass = AppSettings.GraphQL::class)
        val security = instantiateConfig(config = config, keyPath = "ktor.security", kClass = AppSettings.Security::class)
        return AppSettings(global = global, deployment = deployment, security = security, graphql=graphql)
    }

    /**
     * Dynamically instantiates an object of the specified KClass using the primary constructor.
     * Supports both simple and nested data class types. For each constructor parameter,
     * fetches the corresponding configuration value from the specified key path.
     *
     * Data classes constructor parameters and setting key names must match exactly.
     *
     * @param config The application configuration object.
     * @param keyPath The base path in the configuration for fetching values.
     * @param kClass The KClass of the type to instantiate.
     * @return An instance of the specified class with properties populated from the configuration.
     * @throws IllegalArgumentException If a required configuration key is missing or if there is a type mismatch.
     */
    private fun <T : Any> instantiateConfig(config: ApplicationConfig, keyPath: String, kClass: KClass<T>): T {
        val constructor: KFunction<T> = kClass.primaryConstructor!!

        val arguments: Map<KParameter, Any?> = constructor.parameters.associateWith { parameter ->
            // Extracts the Kotlin Class (KClass) from the parameter's type using jvmErasure,
            // which resolves the Kotlin type to its JVM representation. This is crucial for
            // determining the correct class at runtime, particularly for handling complex data structures.
            val parameterType: KClass<*> = parameter.type.jvmErasure

            // Constructs the specific key path for this parameter by appending the parameter's name
            // to the base key path, used to locate the corresponding value in the configuration
            // for this class parameter.
            val parameterKeyPath = "$keyPath.${parameter.name}"

            if (parameterType.isData) {
                // For nested data classes.
                instantiateConfig(config = config, keyPath = parameterKeyPath, kClass = parameterType)
            } else {
                // For simple types convert the value to the correct type.
                convertToType(config = config, keyPath = parameterKeyPath, type = parameterType)
            }
        }

        return constructor.callBy(args = arguments)
    }

    /**
     * Converts a configuration property to the given type.
     *
     * Retrieves a property from the configuration based on the keyPath and converts it
     * to the specified type. For data classes, it recursively instantiates them.
     *
     * @param config The application configuration object.
     * @param keyPath The key path for the property in the configuration.
     * @param type The KClass to which the property should be converted.
     * @return The converted property value or null if not found.
     * @throws IllegalArgumentException for unsupported types or conversion failures.
     */
    private fun convertToType(config: ApplicationConfig, keyPath: String, type: KClass<*>): Any? {
        if (type.isData) {
            // Recurse for nested data classes, like Security.SecretKey, etc.
            return instantiateConfig(config = config, keyPath = keyPath, kClass = type)
        }

        // Resolve simple types.
        val stringValue: String = config.propertyOrNull(path = keyPath)?.getString() ?: return null
        return when (type) {
            String::class -> stringValue
            Int::class -> stringValue.toIntOrNull()
            Boolean::class -> stringValue.toBoolean()
            Long::class -> stringValue.toLongOrNull()
            Double::class -> stringValue.toDoubleOrNull()
            GraphQLFramework::class -> GraphQLFramework.valueOf(stringValue)
            else -> throw IllegalArgumentException("Unsupported type: $type. Found in path: $keyPath")
        }
    }
}