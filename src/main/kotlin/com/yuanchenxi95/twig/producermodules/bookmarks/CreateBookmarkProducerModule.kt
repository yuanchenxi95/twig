package com.yuanchenxi95.twig.producermodules.bookmarks

import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredUrl
import com.yuanchenxi95.twig.protobuf.api.Bookmark
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
import java.net.URL

@Component
class CreateBookmarkProducerModule {
    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var urlRepository: UrlRepository

    @Autowired
    lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    lateinit var urlStreamProducer: UrlStreamProducer

    @Autowired
    lateinit var uuidUtils: UuidUtils

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

    private fun createBookmark(request: CreateBookmarkRequest, authentication: TwigAuthenticationToken): Mono<StoredBookmark> {
        val url = request.url
        val storedUrlMono = r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where(StoredUrl::url.name).`is`(url)),
            StoredUrl::class.java
        )

        val nextId = uuidUtils.generateUUID()
        return storedUrlMono.switchIfEmpty(this.createUrl(url))
            .flatMap {
                val storedBookmark = StoredBookmark(
                    id = nextId,
                    urlId = it.id,
                    // TODO(yuanchenxi), uses the user Id from request context.
                    userId = authentication.getUserId()
                )
                r2dbcEntityTemplate.insert(storedBookmark)
            }.flatMap {
                bookmarkRepository.findById(nextId)
            }
    }

    fun transactionRunner(request: CreateBookmarkRequest, authentication: TwigAuthenticationToken): Mono<StoredBookmark> {
        val operator = TransactionalOperator.create(reactiveTransactionManager)
        return createBookmark(request, authentication).`as`(operator::transactional)
    }

    fun execute(request: CreateBookmarkRequest, authentication: TwigAuthenticationToken): Mono<CreateBookmarkResponse> {
        return transactionRunner(request, authentication).map {
            CreateBookmarkResponse.newBuilder()
                .setBookmark(
                    Bookmark.newBuilder().setId(it.id)
                        .setUrl(request.url)
                        .build()
                )
                .build()
        }
    }
}
