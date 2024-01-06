/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.api.graphql.frameworks.expedia.utils

import graphql.GraphqlErrorException
import graphql.execution.DataFetcherResult
import kcrud.base.exceptions.shared.BaseError
import kcrud.base.exceptions.shared.KcrudException

object GraphQLResult {
    fun <T : Any?> of(data: T, error: BaseError?): DataFetcherResult<T> {

        if (error != null) {
            val graphQLError = GraphqlErrorException.newErrorException()
                .cause(KcrudException(error = error))
                .extensions(mapOf("code" to error.code, "status" to error.status.value))
                .message(error.description)
                .build()

            return DataFetcherResult.newResult<T>()
                .data(data)
                .error(graphQLError)
                .build()
        } else {
            return DataFetcherResult.newResult<T>()
                .data(data)
                .build()
        }
    }
}
