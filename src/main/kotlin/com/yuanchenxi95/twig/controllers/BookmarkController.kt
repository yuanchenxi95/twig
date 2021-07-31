package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.producermodules.bookmarks.CreateBookmarkProducerModule
import com.yuanchenxi95.twig.producermodules.bookmarks.UpdateBookmarkProducerModule
import com.yuanchenxi95.twig.protobuf.api.*
import com.yuanchenxi95.twig.validators.validateCreateBookmarkRequest
import com.yuanchenxi95.twig.validators.validateUpdateBookmarkRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class BookmarkController {
    @Autowired
    private lateinit var createBookmarkProducerModule: CreateBookmarkProducerModule

    @Autowired
    private lateinit var updateBookmarkProducerModule: UpdateBookmarkProducerModule

    @PostMapping(RequestMappingValues.CREATE_BOOKMARK)
    fun createBookmark(
        @RequestBody request: CreateBookmarkRequest,
        authentication: TwigAuthenticationToken
    ): Mono<CreateBookmarkResponse> {
        validateCreateBookmarkRequest(request)
        return createBookmarkProducerModule.Executor(request, authentication).execute()
    }

    @GetMapping(RequestMappingValues.LIST_BOOKMARK)
    fun listBookmarks(authentication: Authentication): Mono<ListBookmarkResponse> {
        TODO("Not yet implemented")
    }

    @PutMapping(RequestMappingValues.UPDATE_BOOKMARK)
    fun updateBookmarks(
        @RequestBody request: UpdateBookmarkRequest,
        @PathVariable(RequestMappingValues.BOOKMARK_ID) bookmarkId: String,
        authentication: TwigAuthenticationToken
    ): Mono<UpdateBookmarkResponse> {
        validateUpdateBookmarkRequest(request, bookmarkId)
        return updateBookmarkProducerModule.Executor(request, bookmarkId, authentication).execute()
    }
}
