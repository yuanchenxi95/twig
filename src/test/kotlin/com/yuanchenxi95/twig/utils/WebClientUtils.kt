package com.yuanchenxi95.twig.utils

import com.google.protobuf.Message
import com.yuanchenxi95.twig.framework.codecs.decodeProtobuf
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import reactor.core.publisher.Flux

fun <T : Message> getResponse(responseSpec: WebTestClient.ResponseSpec, messageClass: T): Flux<T> {
    return responseSpec.returnResult<String>()
        .responseBody.map {
            val builder = messageClass.newBuilderForType()
            decodeProtobuf(it, builder)
            @Suppress("UNCHECKED_CAST")
            builder.build() as T
        }
}
