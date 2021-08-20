package com.yuanchenxi95.twig.producermodules.tags

import com.yuanchenxi95.twig.exceptions.OperationFailedException
import com.yuanchenxi95.twig.exceptions.ResourceNotFoundException
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.producermodules.ProducerModule
import com.yuanchenxi95.twig.protobuf.api.DeleteTagResponse
import com.yuanchenxi95.twig.protobuf.api.deleteTagResponse
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
class DeleteTagProducerModule : ProducerModule<DeleteTagResponse> {

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    inner class Executor(
        private val tagId: String,
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<DeleteTagResponse> {
        private val tagQuery = Query.query(
            Criteria.where(StoredTag::id.name).`is`(tagId)
                .and(StoredTag::userId.name).`is`(authentication.getUserId())
        )

        private fun validateTagExist(): Mono<Void> {
            return r2dbcEntityTemplate.selectOne(tagQuery, StoredTag::class.java)
                .switchIfEmpty(Mono.defer { throw ResourceNotFoundException("Tag '$tagId' not found.") })
                .then()
        }

        private fun deleteTagsBookmarks(): Mono<Boolean> {
            return r2dbcEntityTemplate.delete(
                Query.query(Criteria.where(StoredTagsBookmarks::tagId.name).`is`(tagId)),
                StoredTagsBookmarks::class.java
            ).flatMap(::mapDeleteIntToBoolean)
        }

        private fun deleteTag(): Mono<Boolean> {
            return r2dbcEntityTemplate.delete(
                Query.query(
                    Criteria.where(StoredTag::id.name).`is`(tagId)
                        .and(StoredTag::userId.name).`is`(authentication.getUserId())
                ),
                StoredTag::class.java
            ).flatMap(::mapDeleteIntToBoolean)
        }

        private fun transactionRunner(): Mono<Boolean> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return validateTagExist().then(deleteTagsBookmarks()).then(deleteTag())
                .`as`(operator::transactional)
        }

        override fun execute(): Mono<DeleteTagResponse> {
            return transactionRunner().switchIfEmpty(
                Mono.defer {
                    throw OperationFailedException("Delete tag '$tagId' failed.")
                }
            ).map {
                deleteTagResponse { }
            }
        }
    }
}
