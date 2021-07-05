package com.yuanchenxi95.twig.producermodules.tags

import com.yuanchenxi95.twig.exceptions.ResourceNotFoundException
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.protobuf.api.DeleteTagResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class DeleteTagProducerModule {

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    private fun deleteTag(tagName: String, authentication: TwigAuthenticationToken): Mono<Boolean> {
        return r2dbcEntityTemplate.delete(
            Query.query(Criteria.where(StoredTag::tagName.name).`is`(tagName).and(StoredTag::userId.name).`is`(authentication.getUserId())),
            StoredTag::class.java
        ).flatMap {
            when (it) {
                0 -> Mono.empty()
                else -> Mono.just(true)
            }
        }
    }

    fun transactionRunner(tagName: String, authentication: TwigAuthenticationToken): Mono<Boolean> {
        val operator = TransactionalOperator.create(reactiveTransactionManager)
        return deleteTag(tagName, authentication).`as`(operator::transactional)
    }

    fun execute(tagName: String, authentication: TwigAuthenticationToken): Mono<DeleteTagResponse> {
        return transactionRunner(tagName, authentication).switchIfEmpty(
            Mono.defer {
                throw ResourceNotFoundException("No such Tag.")
            }
        ).map {
            DeleteTagResponse.newBuilder()
                .build()
        }
    }
}
