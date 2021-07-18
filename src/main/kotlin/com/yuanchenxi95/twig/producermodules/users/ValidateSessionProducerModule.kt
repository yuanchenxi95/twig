package com.yuanchenxi95.twig.producermodules.users

import com.yuanchenxi95.twig.models.StoredSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Component
class ValidateSessionProducerModule {

    @Autowired
    lateinit var redisSessionTemplate: ReactiveRedisTemplate<String, StoredSession>

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    fun validateStoredSession(sessionId: String): Mono<StoredSession> {
        return redisSessionTemplate.opsForValue().get(sessionId)
    }

    fun transactionRunner(sessionId: String): Mono<StoredSession?> {
        val operator = TransactionalOperator.create(reactiveTransactionManager)
        return validateStoredSession(sessionId)
            .`as`(operator::transactional)
    }

    fun execute(sessionId: String): Mono<StoredSession?> {
        return transactionRunner(sessionId)
    }
}
