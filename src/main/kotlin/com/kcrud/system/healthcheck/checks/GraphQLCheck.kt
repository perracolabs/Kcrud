/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.system.healthcheck.checks

import com.kcrud.graphql.utils.GraphQLFramework
import com.kcrud.settings.AppSettings
import com.kcrud.utils.DeploymentType
import kotlinx.serialization.Serializable

@Serializable
data class GraphQLCheck(
    val errors: MutableList<String> = mutableListOf(),
    val enabled: Boolean = AppSettings.graphql.isEnabled,
    val framework: GraphQLFramework = AppSettings.graphql.framework,
    val playground: Boolean = AppSettings.graphql.playground,
    val dumpSchema: Boolean = AppSettings.graphql.dumpSchema
) {
    init {
        val className = this::class.simpleName
        val deploymentType = AppSettings.deployment.type

        if (deploymentType == DeploymentType.PROD) {
            if (playground)
                errors.add("$className. GraphQL Playground is enabled in '$deploymentType' environment.")

            if (dumpSchema)
                errors.add("$className. GraphQL Schema Dump is enabled in '$deploymentType' environment.")
        }
    }
}
