package com.yuanchenxi95.twig.framework.error_handlers

import com.yuanchenxi95.twig.constants.DEFAULT_TWIG_INTERNAL_ERROR
import com.yuanchenxi95.twig.framework.codecs.encodeProtobufValue
import io.sentry.Sentry
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class GlobalErrorHandler : ErrorWebExceptionHandler {
    override fun handle(serverWebExchange: ServerWebExchange, exception: Throwable): Mono<Void> {
        print(exception)
        Sentry.captureException(exception)
        val response = serverWebExchange.response
        val bufferFactory = response.bufferFactory()
        response.headers.contentType = MediaType.APPLICATION_JSON
        response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        val dataBuffer = encodeProtobufValue(DEFAULT_TWIG_INTERNAL_ERROR, bufferFactory)
        return response.writeWith(Mono.just(dataBuffer))
    }
}
