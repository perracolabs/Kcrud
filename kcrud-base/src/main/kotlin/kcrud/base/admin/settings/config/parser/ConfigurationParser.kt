/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.admin.settings.config.parser

import io.ktor.server.config.*
import kcrud.base.admin.settings.annotation.ConfigurationAPI
import kcrud.base.admin.settings.config.ConfigurationCatalog
import kcrud.base.utils.Tracer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Automates the parsing of application configuration settings into Kotlin data classes.
 * Utilizes reflection for mapping configuration paths to data class types, supporting
 * both nested and simple structures in line with HOCON standards.
 *
 * Requirements for effective parsing:
 * - Data class properties must match configuration keys exactly.
 * - Configuration paths should mirror the data class hierarchy for proper mapping.
 * - The configuration file needs to be properly formatted according to HOCON specifications.
 *
 * Additionally, the parser handles list configurations as comma-separated strings or actual lists,
 * facilitating simpler configuration in environment variables. This allows settings defined as lists
 * in HOCON to be represented as comma-delimited strings in environmental variables.
 */
@ConfigurationAPI
internal object ConfigurationParser {
    private val tracer = Tracer<ConfigurationParser>()

    /**
     * Performs the application configuration parsing.
     *
     * @param configuration The application configuration object to be parsed.
     * @param mappings Map of top-level configuration paths to their corresponding classes.
     * @return A new [ConfigurationCatalog] object populated with the parsed configuration data.
     */
    fun parse(configuration: ApplicationConfig, mappings: List<ConfigClassMap>): ConfigurationCatalog {
        // Retrieve the primary constructor of the generic type for parameter mapping.
        val constructor: KFunction<ConfigurationCatalog> = ConfigurationCatalog::class.primaryConstructor
            ?: throw IllegalArgumentException("Primary constructor is required for ${ConfigurationCatalog::class.simpleName}.")

        // Create a map between each constructor parameter with its corresponding name as key.
        val constructorParameters: Map<String, KParameter> = constructor.parameters.associateBy { it.name!! }

        // Map each configuration path to its corresponding class, instantiating classes for each setting.
        // Nested settings are handled recursively.
        val arguments: Map<KParameter, Any> = mappings.mapNotNull { configClassMap ->
            val configInstance: Any = instantiateConfig(
                config = configuration,
                keyPath = configClassMap.path,
                kClass = configClassMap.kClass
            )

            // Add into the resulting arguments map the corresponding constructor parameter
            // linked to the newly instantiated configuration class.
            constructorParameters[configClassMap.argument]?.let { parameter ->
                parameter to configInstance
            }
        }.toMap()

        // Create an instance of the output type with the mapped configuration values.
        return constructor.callBy(args = arguments)
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
        tracer.debug("Parsing '${kClass.simpleName}' from '$keyPath'")

        // Fetch the primary constructor of the class.
        val constructor: KFunction<T> = kClass.primaryConstructor!!

        // Map each constructor parameter to its corresponding value from the configuration.
        // This includes direct value assignment for simple types and recursive instantiation for nested data classes.
        val arguments: Map<KParameter, Any?> = constructor.parameters.associateWith { parameter ->
            val parameterType: KClass<*> = parameter.type.jvmErasure
            val parameterKeyPath = "$keyPath.${parameter.name}"

            if (parameterType.isData) {
                // Recursive instantiation for nested data classes.
                instantiateConfig(config = config, keyPath = parameterKeyPath, kClass = parameterType)
            } else {
                // Conversion for simple types.
                val property: KProperty1<T, *> = kClass.memberProperties.find { it.name == parameter.name }!!
                convertToType(config = config, keyPath = parameterKeyPath, type = parameterType, property = property)
            }
        }

        // Create an instance of the class with the obtained configuration values.
        return runCatching {
            constructor.callBy(args = arguments)
        }.getOrElse {
            throw IllegalArgumentException(
                "Error instantiating class $kClass at '$keyPath':\n${it.message}.\nArguments: $arguments"
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
            val listType: KClass<*> = (property.returnType.arguments.first().type!!.classifier as? KClass<*>)!!
            return parseListValues(config = config, keyPath = keyPath, listType = listType)
        }

        // Handle simple types.
        val stringValue: String = config.tryGetString(keyPath) ?: return null
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

            type == Boolean::class -> stringValue.toBooleanStrictOrNull()
                ?: throw IllegalArgumentException("Invalid Boolean value in: '$key'")

            type == Int::class -> stringValue.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid Int value in: '$key'")

            type == Long::class -> stringValue.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid Long value in: '$key'")

            type == Double::class -> stringValue.toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid Double value in: '$key'")

            type.java.isEnum -> convertToEnum(enumType = type, stringValue = stringValue, keyPath = keyPath)

            else -> throw IllegalArgumentException("Unsupported type '$type' in '$key'")
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
            ?: throw IllegalArgumentException(
                "Enum value '$stringValue' not found for type: $enumType. Found in path: $keyPath"
            )
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
            config.tryGetStringList(keyPath) ?: listOf()
        } catch (e: Exception) {
            // If failed to get a list, then treat it as a single string with comma-delimited values.
            val stringValue: String = config.tryGetString(keyPath) ?: ""

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
