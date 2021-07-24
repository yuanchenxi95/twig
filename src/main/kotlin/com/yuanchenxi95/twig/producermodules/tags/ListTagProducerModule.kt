package com.yuanchenxi95.twig.producermodules.tags

import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest
import com.yuanchenxi95.twig.protobuf.api.ListTagRequest
import com.yuanchenxi95.twig.protobuf.api.ListTagResponse
import com.yuanchenxi95.twig.protobuf.api.Tag
import com.yuanchenxi95.twig.repositories.TagRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ListTagProducerModule {
    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var uuidUtils: UuidUtils

    private fun listTag(request: ListTagRequest, authentication: TwigAuthenticationToken): Flux<StoredTag> {
        val maxResults = request.maxResults
        val nextPageToken = request.nextPageToken

        val storedTagFlux = r2dbcEntityTemplate.select(
            Query.query(Criteria.where(StoredTag::userId.name).`is`(authentication.getUserId())),
            StoredTag::class.java
        )

        return storedTagFlux;
    }

    fun transactionRunner(request: ListTagRequest, authentication: TwigAuthenticationToken): Flux<StoredTag> {
        val operator = TransactionalOperator.create(reactiveTransactionManager)
        return listTag(request, authentication).`as`(operator::transactional)

    }

    fun execute(request: ListTagRequest, authentication: TwigAuthenticationToken): Mono<ListTagResponse> {
        val responseBuilder = ListTagResponse.newBuilder()
        transactionRunner(request, authentication).map {
            val tag = Tag.newBuilder().setId(it.id).setName(it.tagName)
            responseBuilder.addTags(tag)
        }
        return Mono.just(responseBuilder.build())
    }
}
