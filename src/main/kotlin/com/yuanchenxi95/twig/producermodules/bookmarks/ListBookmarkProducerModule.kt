package com.yuanchenxi95.twig.producermodules.bookmarks

import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.ListBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.ListBookmarkResponse
import com.yuanchenxi95.twig.repositories.BookmarkRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class ListBookmarkProducerModule {
    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    lateinit var uuidUtils: UuidUtils

    private fun listBookmark(
        request: ListBookmarkRequest,
        authentication: TwigAuthenticationToken
    ): Mono<List<StoredBookmark>> {
        val userId = authentication.getUserId()
        return r2dbcEntityTemplate.select(StoredBookmark::class.java)
            .matching(Query.query(Criteria.where(StoredBookmark::userId.name).`is`(userId)))
            .all()
            .collectList()
    }

    fun transactionRunner(request: ListBookmarkRequest, authentication: TwigAuthenticationToken): Mono<List<StoredBookmark>> {
        val operator = TransactionalOperator.create(reactiveTransactionManager)
        return listBookmark(request, authentication).`as`(operator::transactional)
    }

    fun execute(request: ListBookmarkRequest, authentication: TwigAuthenticationToken): Mono<ListBookmarkResponse> {
        return transactionRunner(request, authentication).map {
            val bookmarks = it.map{ storedBookmark -> {
                Bookmark.newBuilder()
            }}
            ListBookmarkResponse.newBuilder()
                .addAllBookmarks()
                .build()
        }
    }
}
