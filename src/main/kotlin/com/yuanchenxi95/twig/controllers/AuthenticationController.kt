package com.yuanchenxi95.twig.controllers

import com.google.protobuf.Empty
import com.google.protobuf.empty
import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.modelservices.StoredSessionService
import com.yuanchenxi95.twig.utils.httputils.generateResponseCookie
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration

@RestController
class AuthenticationController {

    @Autowired
    lateinit var storedSessionService: StoredSessionService

    @PostMapping(RequestMappingValues.SIGN_OUT)
    fun deleteSession(
        serverWebExchange: ServerWebExchange,
        authentication: TwigAuthenticationToken
    ): Mono<Empty> {
        serverWebExchange.response.addCookie(
            generateResponseCookie(
                HttpHeaders.AUTHORIZATION,
                "",
                Duration.ofSeconds(0)
            )
        )

        return storedSessionService.deleteSession(authentication.getSessionId())
            .map {
                empty { }
            }
    }

    @GetMapping(RequestMappingValues.AUTHENTICATION_CALLBACK)
    fun callback(serverWebExchange: ServerWebExchange) {
        val response = serverWebExchange.response
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(RequestMappingValues.APP_PATH)
    }
}
