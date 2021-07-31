package com.yuanchenxi95.twig.producermodules.users

import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.producermodules.ProducerModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class ValidateSessionProducerModule : ProducerModule<StoredSession> {

    @Autowired
    lateinit var redisSessionTemplate: ReactiveRedisTemplate<String, StoredSession>

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    inner class Executor(
        private val sessionId: String
    ) : ProducerModule.ProducerModuleExecutor<StoredSession?> {

        private fun validateStoredSession(sessionId: String): Mono<StoredSession> {
            return redisSessionTemplate.opsForValue().get(sessionId)
        }

        private fun transactionRunner(sessionId: String): Mono<StoredSession?> {
            val operator = TransactionalOperator.create(reactiveTransactionManager)
            return validateStoredSession(sessionId)
                .`as`(operator::transactional)
        }

        override fun execute(): Mono<StoredSession?> {
            return transactionRunner(sessionId)
        }
    }
}
