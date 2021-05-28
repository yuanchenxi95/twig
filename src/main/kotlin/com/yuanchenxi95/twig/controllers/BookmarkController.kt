package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.converters.BookmarkConverter
import com.yuanchenxi95.twig.daos.BookmarkRepository
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.stream.Collectors.*

// TODO(yuanchenxi95), Add request validation modules.

@RestController
class BookmarkController {

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var bookmarkConverter: BookmarkConverter

    @PostMapping(RequestMappingValues.CREATE_BOOKMARK)
    fun createBookmark(@RequestBody request: CreateBookmarkRequest): Mono<CreateBookmarkResponse> {
        val bookmark = request.bookmark
        return bookmarkRepository.save(
            StoredBookmark(
                hostname = bookmark.hostname,
                uri = bookmark.uri,
                protocol = bookmark.protocol
            )
        )
            .map {
                storedBookmark ->
                val bookmarkCreated = bookmarkConverter.reverse().convert(storedBookmark)
                CreateBookmarkResponse.newBuilder().setBookmark(bookmarkCreated).build()
            }
    }

    @GetMapping(RequestMappingValues.LIST_BOOKMARK)
    fun listBookmarks(@RequestParam(required = false) hostname: String?): Mono<ListBookmarkResponse> {
        val bookmarkConverterReverse = bookmarkConverter.reverse()
        val storedBookmarks = if (hostname == null) bookmarkRepository.findAll() else
            bookmarkRepository.findByHostname(hostname)

        return storedBookmarks.collect(toList()).map(bookmarkConverterReverse::convertAll)
            .map {
                ListBookmarkResponse.newBuilder().addAllBookmarks(it).build()
            }
    }
}
