/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.settings

import com.kcrud.system.Tracer
import io.ktor.server.config.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Creates an AppSettings instance by parsing the application configuration data.
 * This ensures strongly typed access to the configuration data throughout the application.
 */
@SettingsAPI
internal object SettingsParser {
    private val tracer = Tracer<SettingsParser>()

    /**
     * Performs the configuration file parsing.
     *
     * @param config Configuration data parsed from the application's settings file.
     * @param configMappings Map of configuration paths to their corresponding classes.
     * @return A new AppSettings object populated with the parsed configuration data.
     */
    fun parse(config: ApplicationConfig, configMappings: Map<String, KClass<*>>): AppSettings {
        val constructor: KFunction<AppSettings> = AppSettings::class.primaryConstructor!!
        val constructorParameters: Map<String, KParameter> = constructor.parameters.associateBy { it.name!! }

        val settings: Map<KParameter, Any> = configMappings.mapNotNull { (keyPath, kClass) ->
            val configInstance: Any = instantiateConfig(config = config, keyPath = keyPath, kClass = kClass)
            val argumentKey = kClass.simpleName!!.lowercase()
            constructorParameters[argumentKey]!! to configInstance
        }.toMap()

        return constructor.callBy(settings)
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
        tracer.debug("Loading configuration: ${kClass.simpleName}")

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
                val property: KProperty1<T, *> = kClass.memberProperties.find { it.name == parameter.name }!!
                convertToType(config = config, keyPath = parameterKeyPath, type = parameterType, property = property)
            }
        }

        return runCatching {
            constructor.callBy(args = arguments)
        }.getOrElse {
            throw IllegalArgumentException(
                "Error instantiating settings class.\nClass: $kClass\nError: ${it.message}\nPath: $keyPath\nArguments:\n$arguments"
            )
        }
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
     * @param property The property attribute from the type's KClass.
     * @return The converted property value or null if not found.
     * @throws IllegalArgumentException for unsupported types or conversion failures.
     */
    private fun convertToType(config: ApplicationConfig, keyPath: String, type: KClass<*>, property: KProperty1<*, *>): Any? {
        // Handle data classes. Recursively instantiate them.
        if (type.isData) {
            return instantiateConfig(config = config, keyPath = keyPath, kClass = type)
        }

        // Handle lists.
        if (type == List::class) {
            val listType = (property.returnType.arguments.first().type!!.classifier as? KClass<*>)!!
            return parseListValues(config = config, keyPath = keyPath, listType = listType)
        }

        // Handle simple types.
        val stringValue: String = config.propertyOrNull(path = keyPath)?.getString() ?: return null
        return parseElementValue(keyPath = keyPath, stringValue = stringValue, type = type)
    }

    /**
     * Function to parse an element into its final value.
     *
     * @param keyPath The key path for the property in the configuration.
     * @param stringValue The string value to convert.
     * @param type The KClass to which the property should be converted.
     * @return The converted property value or null if not found.
     * @throws IllegalArgumentException For unsupported types or conversion failures.
     */
    private fun parseElementValue(keyPath: String, stringValue: String, type: KClass<*>): Any? {
        val key = "$keyPath: $stringValue"

        return when {
            type == String::class -> stringValue
            type == Boolean::class -> stringValue.toBooleanStrictOrNull() ?: throw IllegalArgumentException("Invalid Boolean. $key")
            type == Int::class -> stringValue.toIntOrNull() ?: throw IllegalArgumentException("Invalid Int. $key")
            type == Long::class -> stringValue.toLongOrNull() ?: throw IllegalArgumentException("Invalid Long. $key")
            type == Double::class -> stringValue.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid Double. $key")
            type.java.isEnum -> convertToEnum(enumType = type, stringValue = stringValue, keyPath = keyPath)
            else -> throw IllegalArgumentException("Unsupported type: $type. Found in path: $keyPath")
        }
    }

    /**
     * Converts a string value to an enum.
     *
     * @param enumType The enum type to which the string value should be converted.
     * @param stringValue The string value to convert.
     * @param keyPath The key path for the property in the configuration.
     * @return The converted enum value or null if not found.
     * @throws IllegalArgumentException If the enum value is not found.
     */
    private fun convertToEnum(enumType: KClass<*>, stringValue: String, keyPath: String): Enum<*>? {
        if (stringValue.isBlank() || stringValue.lowercase() == "null") {
            return null
        }

        return enumType.java.enumConstants.firstOrNull {
            (it as Enum<*>).name.compareTo(stringValue, ignoreCase = true) == 0
        } as Enum<*>?
            ?: throw IllegalArgumentException("Enum value '$stringValue' not found for type: $enumType. Found in path: $keyPath")
    }

    /**
     * Parses a list from the configuration.
     * Lists can be specified as a single string, comma-separated, or as a list of strings.
     * The list is mapped to the specified type.
     *
     * @param config The application configuration object.
     * @param keyPath The key path for the property in the configuration.
     * @param listType The KClass to which the list elements should be converted.
     * @return The converted list or an empty list if not found.
     */
    private fun parseListValues(config: ApplicationConfig, keyPath: String, listType: KClass<*>): List<Any?> {
        val rawList: List<String> = try {
            // Attempt to retrieve it as a list.
            config.propertyOrNull(keyPath)?.getList() ?: listOf()
        } catch (e: Exception) {
            // If failed to get a list, then treat it as a single string with comma-delimited values.
            val stringValue: String = config.propertyOrNull(keyPath)?.getString() ?: ""

            if (stringValue.contains(',')) {
                stringValue.split(',').map { it.trim() }
            } else {
                listOf(stringValue.trim())
            }
        }

        // Map each element of the list to its respective type.
        return rawList.map { listElementValue ->
            parseElementValue(keyPath = keyPath, stringValue = listElementValue, type = listType)
        }
    }
}
