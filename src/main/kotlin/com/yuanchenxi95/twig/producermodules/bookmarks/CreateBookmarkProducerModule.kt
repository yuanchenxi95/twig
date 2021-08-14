package com.yuanchenxi95.twig.producermodules.bookmarks

import com.yuanchenxi95.twig.converters.BookmarkConverter
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.models.StoredUrl
import com.yuanchenxi95.twig.modelservices.StoredTagService
import com.yuanchenxi95.twig.modelservices.StoredTagsBookmarksService
import com.yuanchenxi95.twig.producermodules.ProducerModule
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkResponse
import com.yuanchenxi95.twig.repositories.BookmarkRepository
import com.yuanchenxi95.twig.repositories.UrlRepository
import com.yuanchenxi95.twig.streams.UrlStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.util.function.Tuple3
import reactor.util.function.convert
import java.net.URL

@Component
class CreateBookmarkProducerModule : ProducerModule<CreateBookmarkResponse> {
    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var urlRepository: UrlRepository

    @Autowired
    lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    lateinit var bookmarkConverter: BookmarkConverter

    @Autowired
    lateinit var storedTagService: StoredTagService

    @Autowired
    lateinit var storedTagsBookmarkService: StoredTagsBookmarksService

    @Autowired
    lateinit var urlStreamProducer: UrlStreamProducer

    @Autowired
    lateinit var uuidUtils: UuidUtils

    inner class Executor(
        private val request: CreateBookmarkRequest,
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<CreateBookmarkResponse> {

        private fun createUrl(url: String): Mono<StoredUrl> {
            val parsedUrl = URL(url)
            val nextId = uuidUtils.generateUUID()
            val storedUrl = StoredUrl(
                id = nextId,
                protocol = parsedUrl.protocol,
                host = parsedUrl.host,
                path = parsedUrl.path,
                url = url
            )
            return r2dbcEntityTemplate.insert(storedUrl).flatMap {
                urlRepository.findById(nextId)
            }.doOnNext {
                urlStreamProducer.publishUrlEvent(it)
            }
        }

        private fun createTagsAndLinkTagsForBookmark(bookmarkId: String): Mono<List<StoredTag>> {
            return storedTagService.queryOrCreateTags(
                authentication.getUserId(),
                request.bookmark.tagsList
            ).flatMap { storedTags ->
                storedTagsBookmarkService.batchCreateReferences(
                    bookmarkId,
                    storedTags.map { it.id }
                ).then(Mono.just(storedTags))
            }
        }

        private fun createBookmark(): Mono<Tuple3<StoredBookmark, StoredUrl, List<StoredTag>>> {
            val url = request.bookmark.url
            val storedUrlMono = r2dbcEntityTemplate.selectOne(
                Query.query(Criteria.where(StoredUrl::url.name).`is`(url)),
                StoredUrl::class.java
            )

            val nextId = uuidUtils.generateUUID()
            return storedUrlMono.switchIfEmpty(this.createUrl(url))
                .flatMap {
                    val storedBookmark = StoredBookmark(
                        id = nextId,
                        displayName = request.bookmark.displayName,
                        urlId = it.id,
                        userId = authentication.getUserId()
                    )

                    Mono.zip(
                        r2dbcEntityTemplate.insert(storedBookmark)
                            .then(bookmarkRepository.findById(nextId)),
                        Mono.just(it),
                        createTagsAndLinkTagsForBookmark(it.id)
                    )
                }
        }

        private fun transactionRunner(): Mono<Tuple3<StoredBookmark, StoredUrl, List<StoredTag>>> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return createBookmark().`as`(operator::transactional)
        }

        override fun execute(): Mono<CreateBookmarkResponse> {
            return transactionRunner().map {
                CreateBookmarkResponse.newBuilder()
                    .setBookmark(
                        bookmarkConverter.doForward(
                            it.convert()
                        )
                    )
                    .build()
            }
        }
    }
}
