package com.yuanchenxi95.twig.controllers

import com.google.protobuf.Empty
import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.modelservices.StoredSessionService
import com.yuanchenxi95.twig.utils.httputils.generateResponseCookie
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class SignOutController {

    @Autowired
    lateinit var storedSessionService: StoredSessionService

    @PostMapping(RequestMappingValues.SIGN_OUT)
    fun deleteTag(
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
                Empty.newBuilder().build()
            }
    }
}
