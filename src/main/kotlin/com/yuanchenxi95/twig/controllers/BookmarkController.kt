package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.producermodules.bookmarks.CreateBookmarkProducerModule
import com.yuanchenxi95.twig.protobuf.api.*
import com.yuanchenxi95.twig.validators.validateCreateBookmarkRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class BookmarkController {
    @Autowired
    private lateinit var createBookmarkProducerModule: CreateBookmarkProducerModule

    @PostMapping(RequestMappingValues.CREATE_BOOKMARK)
    fun createBookmark(@RequestBody request: CreateBookmarkRequest): Mono<CreateBookmarkResponse> {
        validateCreateBookmarkRequest(request)
        return createBookmarkProducerModule.execute(request)
    }

    @GetMapping(RequestMappingValues.LIST_BOOKMARK)
    fun listBookmarks(): Mono<ListBookmarkResponse> {
        TODO("Not yet implemented")
    }
}
