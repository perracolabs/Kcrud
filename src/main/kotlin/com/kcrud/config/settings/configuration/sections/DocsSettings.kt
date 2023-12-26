/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.config.settings.configuration.sections

/**
 * Contains settings related to Swagger, OpenAPI, and Redoc.
 *
 * @property isEnabled Whether Swagger is enabled.
 * @property yamlFile The documentation location file.
 * @property swaggerPath The path to the Swagger UI.
 * @property openApiPath The path to the OpenAPI specification.
 * @property redocPath The path to the Redoc file.
 */
internal data class DocsSettings(
    val isEnabled: Boolean,
    val yamlFile: String,
    val swaggerPath: String,
    val openApiPath: String,
    val redocPath: String
)
