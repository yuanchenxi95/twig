package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.producermodules.bookmarks.CreateBookmarkProducerModule
import com.yuanchenxi95.twig.producermodules.bookmarks.ListBookmarkProducerModule
import com.yuanchenxi95.twig.producermodules.bookmarks.UpdateBookmarkProducerModule
import com.yuanchenxi95.twig.protobuf.api.*
import com.yuanchenxi95.twig.validators.validateCreateBookmarkRequest
import com.yuanchenxi95.twig.validators.validateListBookmarkRequest
import com.yuanchenxi95.twig.validators.validateUpdateBookmarkRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import kotlin.math.min

@RestController
class BookmarkController {
    companion object {
        const val LIST_BOOKMARK_MAX_PAGE_SIZE = 1000
    }

    @Autowired
    private lateinit var createBookmarkProducerModule: CreateBookmarkProducerModule

    @Autowired
    private lateinit var updateBookmarkProducerModule: UpdateBookmarkProducerModule

    @Autowired
    private lateinit var listBookmarkProducerModule: ListBookmarkProducerModule

    @PostMapping(RequestMappingValues.CREATE_BOOKMARK)
    fun createBookmark(
        @RequestBody request: CreateBookmarkRequest,
        authentication: TwigAuthenticationToken
    ): Mono<CreateBookmarkResponse> {
        validateCreateBookmarkRequest(request)
        return createBookmarkProducerModule.Executor(request, authentication).execute()
    }

    @GetMapping(RequestMappingValues.LIST_BOOKMARK)
    fun listBookmarks(
        @RequestParam(value = "page_size", required = false, defaultValue = "50") pageSize: Int,
        @RequestParam(value = "page_token", required = false, defaultValue = "") pageToken: String,
        authentication: TwigAuthenticationToken
    ): Mono<ListBookmarkResponse> {
        validateListBookmarkRequest(pageSize, pageToken)
        return listBookmarkProducerModule.Executor(min(pageSize, LIST_BOOKMARK_MAX_PAGE_SIZE), pageToken, authentication).execute()
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
