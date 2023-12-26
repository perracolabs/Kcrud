/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package com.kcrud.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

/**
 * Configures HTTP default headers.
 *
 * See: [Default Headers Documentation](https://ktor.io/docs/default-headers.html)
 */
fun Application.configureDefaultHeaders() {

    install(DefaultHeaders) {
        header("X-Engine", "Kcrud")
    }
}
