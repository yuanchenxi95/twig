package com.yuanchenxi95.twig.producermodules.users

import com.yuanchenxi95.twig.models.StoredSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class ValidateSessionProducerModule {

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    fun validateStoredSession(sessionId: String): Mono<StoredSession> {
        return r2dbcEntityTemplate.selectOne(
            Query.query(
                Criteria.where(StoredSession::id.name).`is`(sessionId)
            ),
            StoredSession::class.java
        )
            .filter {
                it.expirationTime.isAfter(Instant.now())
            }
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
