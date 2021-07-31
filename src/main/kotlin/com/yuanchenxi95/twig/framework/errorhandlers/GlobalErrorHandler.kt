package com.yuanchenxi95.twig.framework.errorhandlers

import com.yuanchenxi95.twig.application.TwigConfigurations
import com.yuanchenxi95.twig.constants.generateAuthenticationError
import com.yuanchenxi95.twig.constants.generateInternalServerError
import com.yuanchenxi95.twig.exceptions.AuthFailedException
import com.yuanchenxi95.twig.framework.codecs.encodeProtobufValue
import com.yuanchenxi95.twig.utils.httputils.generateResponseCookie
import io.sentry.Sentry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class GlobalErrorHandler : ErrorWebExceptionHandler {
    @Autowired
    lateinit var twigConfigurations: TwigConfigurations

    override fun handle(serverWebExchange: ServerWebExchange, exception: Throwable): Mono<Void> {
        print(exception)
        Sentry.captureException(exception)
        val response = serverWebExchange.response
        val bufferFactory = response.bufferFactory()
        response.headers.contentType = MediaType.APPLICATION_JSON
        response.statusCode = when (exception) {
            is AuthFailedException -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        if (exception is AuthFailedException) {
            response.cookies.set(
                HttpHeaders.AUTHORIZATION,
                generateResponseCookie(HttpHeaders.AUTHORIZATION, "", Duration.ofSeconds(0))
            )
        }
        val dataBuffer = encodeProtobufValue(
            when (exception) {
                is AuthFailedException -> generateAuthenticationError(exception)
                else -> generateInternalServerError(
                    exception,
                    twigConfigurations.showInternalServerError
                )
            },
            bufferFactory
        )

        return response.writeWith(Mono.just(dataBuffer))
    }
}
