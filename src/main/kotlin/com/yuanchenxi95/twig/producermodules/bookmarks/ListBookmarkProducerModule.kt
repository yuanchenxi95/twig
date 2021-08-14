package com.yuanchenxi95.twig.producermodules.bookmarks

import com.yuanchenxi95.twig.converters.BookmarkConverter
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.models.StoredUrl
import com.yuanchenxi95.twig.modelservices.StoredBookmarkService
import com.yuanchenxi95.twig.modelservices.StoredTagService
import com.yuanchenxi95.twig.modelservices.StoredTagsBookmarksService
import com.yuanchenxi95.twig.modelservices.StoredUrlService
import com.yuanchenxi95.twig.producermodules.ProducerModule
import com.yuanchenxi95.twig.protobuf.api.*
import com.yuanchenxi95.twig.repositories.BookmarkRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3

@Component
class ListBookmarkProducerModule : ProducerModule<ListBookmarkResponse> {
    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var bookmarkConverter: BookmarkConverter

    @Autowired
    lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    lateinit var tagService: StoredTagService

    @Autowired
    lateinit var bookmarkService: StoredBookmarkService

    @Autowired
    lateinit var tagsBookmarksService: StoredTagsBookmarksService

    @Autowired
    lateinit var urlService: StoredUrlService

    @Autowired
    lateinit var uuidUtils: UuidUtils

    inner class Executor(
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<ListBookmarkResponse> {

        private fun listStoredBookmark(): Mono<List<StoredBookmark>> {
            return bookmarkService.queryBookmarksForUser(authentication.getUserId())
        }

        private fun getTagsMapById(tagsBookmarks: Mono<List<StoredTagsBookmarks>>): Mono<Map<String, StoredTag>> {
            return tagsBookmarks.map { storedTagsBookmarks ->
                HashSet(
                    storedTagsBookmarks.map {
                        it.tagId
                    }
                )
            }.flatMap { tagIds ->
                tagService.queryTagsForUserByTagIds(authentication.getUserId(), tagIds)
            }.map { storedTagList ->
                storedTagList.associateBy(StoredTag::id)
            }
        }

        private fun listBookmarkWithTag(): Mono<List<Bookmark>> {
            return listStoredBookmark().flatMap { storedBookmark ->

                val bookmarkIds = storedBookmark.map { it.id }
                val tagsBookmarksMono =
                    tagsBookmarksService.queryTagsBookmarksForBookmarks(bookmarkIds)
                val tagsMapByIdMono = getTagsMapById(tagsBookmarksMono)

                val urlIds = storedBookmark.map { it.urlId }
                val urlsMapByIdMono = urlService.selectUrlsByIds(urlIds).map {
                    it.associateBy(StoredUrl::id)
                }

                Mono.zip(tagsBookmarksMono, tagsMapByIdMono, urlsMapByIdMono)
                    .map { tuple ->
                        val (tagsBookmarks, tagsMapById, urlsMapById) = tuple
                        val tagsByBookmarkId = tagsBookmarks
                            .groupBy({ it.bookmarkId }, { tagsMapById[it.tagId]!! })

                        storedBookmark.map { bookmark ->
                            bookmarkConverter.doForward(
                                Triple(
                                    bookmark,
                                    urlsMapById[bookmark.urlId]!!,
                                    tagsByBookmarkId[bookmark.id] ?: listOf()
                                )
                            )
                        }
                    }
            }
        }

        private fun transactionRunner(): Mono<List<Bookmark>> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return listBookmarkWithTag().`as`(operator::transactional)
        }

        override fun execute(): Mono<ListBookmarkResponse> {
            return transactionRunner().map {
                ListBookmarkResponse.newBuilder()
                    .addAllBookmarks(it)
                    .build()
            }
        }
    }
}
