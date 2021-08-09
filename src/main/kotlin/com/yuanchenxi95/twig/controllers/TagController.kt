package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.producermodules.tags.CreateTagProducerModule
import com.yuanchenxi95.twig.producermodules.tags.DeleteTagProducerModule
import com.yuanchenxi95.twig.producermodules.tags.ListTagProducerModule
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest
import com.yuanchenxi95.twig.protobuf.api.CreateTagResponse
import com.yuanchenxi95.twig.protobuf.api.DeleteTagResponse
import com.yuanchenxi95.twig.protobuf.api.ListTagResponse
import com.yuanchenxi95.twig.validators.validateCreateTagRequest
import com.yuanchenxi95.twig.validators.validateDeleteTagRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
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
    fun createTag(
        @RequestBody request: CreateTagRequest,
        authentication: TwigAuthenticationToken
    ): Mono<CreateTagResponse> {
        validateCreateTagRequest(request)
        return createTagProducerModule.Executor(request, authentication).execute()
    }

    @DeleteMapping(RequestMappingValues.DELETE_TAG)
    fun deleteTag(
        @PathVariable(value = RequestMappingValues.TAG_ID) tagId: String,
        authentication: TwigAuthenticationToken
    ): Mono<DeleteTagResponse> {
        validateDeleteTagRequest(tagId)
        return deleteTagProducerModule.Executor(tagId, authentication).execute()
    }

    @GetMapping(RequestMappingValues.LIST_TAG)
    fun listTags(
        authentication: TwigAuthenticationToken
    ): Mono<ListTagResponse> {
        return listTagProducerModule.Executor(authentication).execute()
    }
}
