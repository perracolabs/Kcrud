/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.core.admin.env.healthcheck.checks

import kcrud.core.admin.env.healthcheck.annotation.HealthCheckAPI
import kcrud.core.admin.settings.AppSettings
import kcrud.core.admin.types.EnvironmentType
import kcrud.core.api.graphql.GraphQLFramework
import kotlinx.serialization.Serializable

@Suppress("unused")
@HealthCheckAPI
@Serializable
data class GraphQLCheck(
    val errors: MutableList<String> = mutableListOf(),
    val enabled: Boolean = AppSettings.graphql.isEnabled,
    val framework: GraphQLFramework = AppSettings.graphql.framework,
    val playground: Boolean = AppSettings.graphql.playground,
    val dumpSchema: Boolean = AppSettings.graphql.dumpSchema,
    val schemaPath: String = AppSettings.graphql.schemaPath
) {
    init {
        val className = this::class.simpleName
        val deploymentType = AppSettings.deployment.type

        if (deploymentType == EnvironmentType.PROD) {
            if (playground) {
                errors.add("$className. GraphQL Playground is enabled in '$deploymentType' environment.")
            }

            if (dumpSchema) {
                errors.add("$className. GraphQL Schema Dump is enabled in '$deploymentType' environment.")
            }

            if (dumpSchema && schemaPath.isBlank()) {
                errors.add("$className. GraphQL Schema Dump is enabled but no schema path is provided.")
            }
        }
    }
}
