package com.yuanchenxi95.twig.producermodules.users

import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.models.StoredUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class LoginUserProducerModule {

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var uuidUtils: UuidUtils

    @Autowired
    lateinit var reactiveTransactionManager: ReactiveTransactionManager

    private fun findUserByEmail(email: String): Mono<StoredUser> {
        return r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where(StoredUser::userEmail.name).`is`(email)),
            StoredUser::class.java
        )
    }

    private fun createUser(email: String, name: String): Mono<StoredUser> {
        val nextId = uuidUtils.generateUUID()
        val storedUser = StoredUser(
            id = nextId,
            name = name,
            userEmail = email,
        )
        return r2dbcEntityTemplate.insert(storedUser)
    }

    private fun createSession(userId: String): Mono<StoredSession> {
        // TODO(yuanchenxi95) Take in the expiration time from the config file.
        val expirationDurationInSeconds: Long = 60 * 60 * 24 * 30 // One month.
        val expirationTime = Instant.now().plusSeconds(expirationDurationInSeconds)

        val nextId = uuidUtils.generateUUID()
        val storedSession = StoredSession(
            id = nextId,
            userId,
            expirationTime,
        )
        return r2dbcEntityTemplate.insert(storedSession)
    }

    fun transactionRunner(email: String, name: String): Mono<StoredSession> {
        val operator = TransactionalOperator.create(reactiveTransactionManager)
        return this.findUserByEmail(email)
            .switchIfEmpty(createUser(email, name))
            .flatMap {
                createSession(it.id)
            }
            .`as`(operator::transactional)
    }

    fun execute(securityContext: SecurityContext): Mono<StoredSession> {
        val defaultOAuth2User = securityContext.authentication.principal
        if (defaultOAuth2User !is DefaultOAuth2User) {
            throw IllegalArgumentException("Unsupported principal.")
        }

        val email = defaultOAuth2User.attributes["email"] as String
        val name = defaultOAuth2User.attributes["name"] as String
        // TODO(yuanchenxi95), validates the attributes.

        return transactionRunner(email, name)
    }
}
