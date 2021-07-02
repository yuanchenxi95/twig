package com.yuanchenxi95.twig.producermodules.tags

import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest
import com.yuanchenxi95.twig.protobuf.api.CreateTagResponse
import com.yuanchenxi95.twig.protobuf.api.Tag
import com.yuanchenxi95.twig.repositories.TagRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class CreateTagProducerModule {

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var uuidUtils: UuidUtils

    private fun createTag(request: CreateTagRequest, authentication: TwigAuthenticationToken): Mono<StoredTag> {
        val name = request.name
        val storedTagMono = r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where(StoredTag::tagName.name).`is`(name).and(StoredTag::userId.name).`is`(authentication.getUserId())),
            StoredTag::class.java
        )

        val nextId = uuidUtils.generateUUID()
        return storedTagMono.switchIfEmpty(
            Mono.defer {
                val storedTag = StoredTag(
                    id = nextId,
                    userId = authentication.getUserId(),
                    tagName = name
                )
                r2dbcEntityTemplate.insert(storedTag)
            }
        ).flatMap {
            tagRepository.findById(it.id)
        }
    }

    fun transactionRunner(request: CreateTagRequest, authentication: TwigAuthenticationToken): Mono<StoredTag> {
        val operator = TransactionalOperator.create(reactiveTransactionManager)
        return createTag(request, authentication).`as`(operator::transactional)
    }

    fun execute(request: CreateTagRequest, authentication: TwigAuthenticationToken): Mono<CreateTagResponse> {
        return transactionRunner(request, authentication).map {
            CreateTagResponse.newBuilder()
                .setTag(
                    Tag.newBuilder().setId(it.id)
                        .setName(it.tagName)
                )
                .build()
        }
    }
}
