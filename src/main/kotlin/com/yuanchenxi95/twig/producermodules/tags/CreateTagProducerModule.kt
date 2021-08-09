package com.yuanchenxi95.twig.producermodules.tags

import com.yuanchenxi95.twig.exceptions.OperationFailedException
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.producermodules.ProducerModule
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
class CreateTagProducerModule : ProducerModule<CreateTagResponse> {

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var uuidUtils: UuidUtils

    inner class Executor(
        private val request: CreateTagRequest,
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<CreateTagResponse> {

        private fun validateTagNotExist(): Mono<Void> {
            val name = request.name
            return r2dbcEntityTemplate.selectOne(
                Query.query(
                    Criteria.where(StoredTag::tagName.name).`is`(name).and(StoredTag::userId.name)
                        .`is`(authentication.getUserId())
                ),
                StoredTag::class.java
            )
                .flatMap { Mono.defer { throw OperationFailedException("Tag with name '$name' already exists.") } }
        }

        private fun createTag(): Mono<StoredTag> {
            val name = request.name

            return validateTagNotExist().then(
                Mono.defer {
                    val nextId = uuidUtils.generateUUID()
                    val storedTag = StoredTag(
                        id = nextId,
                        userId = authentication.getUserId(),
                        tagName = name
                    )
                    r2dbcEntityTemplate.insert(storedTag)
                }
            )
        }

        private fun transactionRunner(): Mono<StoredTag> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return createTag().`as`(operator::transactional)
        }

        override fun execute(): Mono<CreateTagResponse> {
            return transactionRunner().map {
                CreateTagResponse.newBuilder()
                    .setTag(
                        Tag.newBuilder().setId(it.id)
                            .setName(it.tagName)
                    )
                    .build()
            }
        }
    }
}
