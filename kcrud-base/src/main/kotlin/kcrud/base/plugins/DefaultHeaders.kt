/*
 * Copyright (c) 2023 Perraco Labs. All rights reserved.
 * This work is licensed under the terms of the MIT license.
 * For a copy, see <https://opensource.org/licenses/MIT>
 */

package kcrud.base.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.*

/**
 * Configures HTTP default headers and auto head response.
 *
 * See: [Default Headers Documentation](https://ktor.io/docs/default-headers.html)
 *
 * See: [Auto Head Response Documentation](https://ktor.io/docs/autoheadresponse.html)
 *
 * See: [Compression Plugin](https://ktor.io/docs/compression.html)
 *
 * See: [Caching Headers Plugin](https://ktor.io/docs/caching.html)
 *
 * See: [X-Forwarded-Header Support Plugin](https://ktor.io/docs/x-forwarded-headers.html)
 */
fun Application.configureHeaders() {

    // Provides with the ability to automatically respond to a HEAD request
    // for every route that has a GET defined.
    // You can use AutoHeadResponse to avoid creating a separate head handler
    // if you need to somehow process a response on the client before getting
    // the actual content. For example, calling the respondFile function adds
    // the Content-Length and Content-Type headers to a response automatically,
    // and you can get this information on the client before downloading the file.
    install(AutoHeadResponse)

    // Adds the standard Server and Date headers into each response.
    // Moreover, you can provide additional default headers and override the Server header.
    install(DefaultHeaders) {
        header(name = "X-Engine", value = "Kcrud")
    }

    // Provides the capability to compress outgoing content.
    // You can use different compression algorithms, including gzip and deflate,
    // specify the required conditions for compressing data, such as a content type
    // or response size, or even compress data based on specific request parameters.
    install(Compression)

    // The CachingHeaders plugin adds the capability to configure
    // the Cache-Control and Expires headers used for HTTP caching.
    install(CachingHeaders)

    // The ForwardedHeaders and XForwardedHeaders plugins allow to handle
    // reverse proxy headers to get information about the original request
    // when a Ktor server is placed behind a reverse proxy.
    // This might be useful for logging purposes.
    install(XForwardedHeaders)
}
