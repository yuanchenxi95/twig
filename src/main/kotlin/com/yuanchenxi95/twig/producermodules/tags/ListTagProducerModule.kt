package com.yuanchenxi95.twig.producermodules.tags

import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.modelservices.StoredTagService
import com.yuanchenxi95.twig.producermodules.ProducerModule
import com.yuanchenxi95.twig.protobuf.api.ListTagResponse
import com.yuanchenxi95.twig.protobuf.api.listTagResponse
import com.yuanchenxi95.twig.protobuf.api.tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class ListTagProducerModule : ProducerModule<ListTagProducerModule> {
    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var storedTagService: StoredTagService

    inner class Executor(
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<ListTagResponse> {

        private fun transactionRunner(): Mono<List<StoredTag>> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return storedTagService.queryTagsForUser(authentication.getUserId())
                .`as`(operator::transactional)
        }

        override fun execute(): Mono<ListTagResponse> {
            return transactionRunner().map {
                listTagResponse {
                    it.forEach { apiTag ->
                        tags.add(
                            tag {
                                id = apiTag.id
                                name = apiTag.tagName
                            }
                        )
                    }
                }
            }
        }
    }
}
