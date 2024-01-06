/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.api.graphql.frameworks.kgraphql.utils

import com.apurebase.kgraphql.GraphQLError
import kcrud.base.exceptions.shared.BaseError
import kcrud.base.exceptions.shared.KcrudException

object GraphQLError {
    fun of(error: BaseError): Nothing {
        val exception = KcrudException(error = error)
        throw GraphQLError(
            message = exception.messageDetail()
        )
    }
}
