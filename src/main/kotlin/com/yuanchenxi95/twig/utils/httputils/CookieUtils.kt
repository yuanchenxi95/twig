package com.yuanchenxi95.twig.utils.httputils

import org.springframework.http.ResponseCookie
import java.time.Duration

fun generateResponseCookie(name: String, value: String, maxAge: Duration): ResponseCookie {
    val responseCookieBuilder = ResponseCookie.from(name, value)
    responseCookieBuilder
        .httpOnly(true)
        .path("/")
        .sameSite("Strict")
        // TODO(yuanchenxi95), check the request is HTTP or HTTPS.
        .secure(false)
        // TODO(yuanchenxi95) Take in the expiration time from the config file.
        .maxAge(maxAge)

    return responseCookieBuilder.build()
}
