package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.producermodules.tags.CreateTagProducerModule
import com.yuanchenxi95.twig.producermodules.tags.DeleteTagProducerModule
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest
import com.yuanchenxi95.twig.protobuf.api.CreateTagResponse
import com.yuanchenxi95.twig.protobuf.api.DeleteTagResponse
import com.yuanchenxi95.twig.validators.validateCreateTagRequest
import com.yuanchenxi95.twig.validators.validateDeleteTagRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import com.yuanchenxi95.twig.producermodules.tags.ListTagProducerModule

import com.yuanchenxi95.twig.protobuf.api.ListTagRequest
import com.yuanchenxi95.twig.protobuf.api.ListTagResponse

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import reactor.core.publisher.Mono

@RestController
class TagController {
    @Autowired
    private lateinit var createTagProducerModule: CreateTagProducerModule

    @Autowired
    private lateinit var deleteTagProducerModule: DeleteTagProducerModule

    @Autowired
    private lateinit var listTagProducerModule: ListTagProducerModule


    @PostMapping(RequestMappingValues.CREATE_TAG)
    fun createTag(@RequestBody request: CreateTagRequest, authentication: TwigAuthenticationToken): Mono<CreateTagResponse> {
        validateCreateTagRequest(request)
        return createTagProducerModule.execute(request, authentication)
    }

    @DeleteMapping(RequestMappingValues.DELETE_TAG)
    fun deleteTag(@PathVariable(value = "tagName") tagName: String, authentication: TwigAuthenticationToken): Mono<DeleteTagResponse> {
        validateDeleteTagRequest(tagName)
        return deleteTagProducerModule.execute(tagName, authentication)
    }

    @GetMapping(RequestMappingValues.LIST_TAG)
    fun listTags(@RequestBody request: ListTagRequest, authentication: TwigAuthenticationToken): Mono<ListTagResponse> {
        return listTagProducerModule.execute(request, authentication)

    }
}
