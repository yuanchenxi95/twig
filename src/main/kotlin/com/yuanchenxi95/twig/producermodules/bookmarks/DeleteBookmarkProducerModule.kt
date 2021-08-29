package com.yuanchenxi95.twig.producermodules.bookmarks

import com.yuanchenxi95.twig.exceptions.OperationFailedException
import com.yuanchenxi95.twig.exceptions.ResourceNotFoundException
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.producermodules.ProducerModule
import com.yuanchenxi95.twig.protobuf.api.DeleteBookmarkResponse
import com.yuanchenxi95.twig.protobuf.api.deleteBookmarkResponse
import com.yuanchenxi95.twig.utils.databaseutils.mapDeleteIntToBoolean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class DeleteBookmarkProducerModule : ProducerModule<DeleteBookmarkResponse> {

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    inner class Executor(
        private val bookmarkId: String,
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<DeleteBookmarkResponse> {
        private val bookmarkQuery = Query.query(
            Criteria.where(StoredBookmark::id.name).`is`(bookmarkId)
                .and(StoredBookmark::userId.name).`is`(authentication.getUserId())
        )

        private fun validateBookmarkExist(): Mono<Void> {
            return r2dbcEntityTemplate.selectOne(bookmarkQuery, StoredBookmark::class.java)
                .switchIfEmpty(Mono.defer { throw ResourceNotFoundException("Bookmark '$bookmarkId' not found.") })
                .then()
        }

        private fun deleteTagsBookmarks(): Mono<Boolean> {
            return r2dbcEntityTemplate.delete(
                Query.query(Criteria.where(StoredTagsBookmarks::bookmarkId.name).`is`(bookmarkId)),
                StoredTagsBookmarks::class.java
            ).flatMap(::mapDeleteIntToBoolean)
        }

        private fun deleteBookmark(): Mono<Boolean> {
            return r2dbcEntityTemplate.delete(
                Query.query(
                    Criteria.where(StoredBookmark::id.name).`is`(bookmarkId)
                        .and(StoredBookmark::userId.name).`is`(authentication.getUserId())
                ),
                StoredBookmark::class.java
            ).flatMap(::mapDeleteIntToBoolean)
        }

        private fun transactionRunner(): Mono<Boolean> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return validateBookmarkExist().then(deleteTagsBookmarks()).then(deleteBookmark())
                .`as`(operator::transactional)
        }

        override fun execute(): Mono<DeleteBookmarkResponse> {
            return transactionRunner().switchIfEmpty(
                Mono.defer {
                    throw OperationFailedException("Delete bookmark '$bookmarkId' failed.")
                }
            ).map {
                deleteBookmarkResponse { }
            }
        }
    }
}
