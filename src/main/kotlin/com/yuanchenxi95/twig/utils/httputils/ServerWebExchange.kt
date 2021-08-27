package com.yuanchenxi95.twig.utils.httputils

import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import java.net.URI

fun redirectTo(serverWebExchange: ServerWebExchange, uri: URI) {
    val response = serverWebExchange.response
    response.statusCode = HttpStatus.FOUND
    response.headers.location = uri
}
