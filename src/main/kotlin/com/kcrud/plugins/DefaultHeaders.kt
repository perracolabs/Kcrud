/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.defaultheaders.*

/**
 * Configures HTTP default headers and auto head response.
 *
 * See: [Default Headers Documentation](https://ktor.io/docs/default-headers.html)
 *
 * See: [Auto Head Response Documentation](https://ktor.io/docs/autoheadresponse.html)
 */
fun Application.configureHeaders() {

    install(AutoHeadResponse)

    install(DefaultHeaders) {
        header(name = "X-Engine", value = "Kcrud")
    }
}
