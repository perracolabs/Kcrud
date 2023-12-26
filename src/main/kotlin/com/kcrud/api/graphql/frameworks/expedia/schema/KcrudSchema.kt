/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.api.graphql.frameworks.expedia.schema

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ContactDirective
import com.expediagroup.graphql.server.Schema
import com.kcrud.api.graphql.frameworks.expedia.ExpediaAPI

@ContactDirective(
    name = "KCrud Schema",
    url = "https://github.com/perracolabs/Kcrud",
    description = "Expedia schema example with Ktor."
)
@GraphQLDescription("Kcrud schema description")
@ExpediaAPI
class KcrudSchema : Schema
