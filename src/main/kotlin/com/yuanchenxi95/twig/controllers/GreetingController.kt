package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.protobuf.api.HelloRequest
import com.yuanchenxi95.twig.protobuf.api.HelloResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/greeting")
class GreetingController {

    @PostMapping(value = ["/hello"])
    fun greeting(@RequestBody request: HelloRequest): Flux<HelloResponse> {
        val greeting = StringBuilder().append("Hello, ").append(request.firstName).append(" ")
            .append(request.lastName).toString()
        return Flux.just(HelloResponse.newBuilder().setGreeting(greeting).build())
    }
}
