package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.producermodules.tags.CreateTagProducerModule
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest
import com.yuanchenxi95.twig.protobuf.api.CreateTagResponse
import com.yuanchenxi95.twig.validators.validateCreateTagRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TagController {
    @Autowired
    private lateinit var createTagProducerModule: CreateTagProducerModule

    @PostMapping(RequestMappingValues.CREATE_TAG)
    fun createTag(@RequestBody request: CreateTagRequest, authentication: TwigAuthenticationToken): Mono<CreateTagResponse> {
        validateCreateTagRequest(request)
        return createTagProducerModule.execute(request, authentication)
    }
}
