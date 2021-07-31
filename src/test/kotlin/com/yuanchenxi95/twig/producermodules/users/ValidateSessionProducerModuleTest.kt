package com.yuanchenxi95.twig.producermodules.users

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_SESSION_1
import com.yuanchenxi95.twig.models.StoredSession
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.test.StepVerifier
import java.time.Duration

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
@Import(
    ValidateSessionProducerModule::class
)
class ValidateSessionProducerModuleTest : AbstractTestBase() {

    @Autowired
    lateinit var validateSessionProducerModule: ValidateSessionProducerModule

    @Autowired
    lateinit var redisSessionTemplate: ReactiveRedisTemplate<String, StoredSession>

    @Test
    fun `validate session success`() {
        redisSessionTemplate.opsForValue().set(STORED_SESSION_1.id, STORED_SESSION_1).block()

        StepVerifier.create(validateSessionProducerModule.Executor(STORED_SESSION_1.id).execute())
            .assertNext {
                assertThat(it!!.id).isEqualTo(STORED_SESSION_1.id)
                assertThat(it.userId).isEqualTo(STORED_SESSION_1.userId)
                assertThat(it.expirationTime).isEqualTo(STORED_SESSION_1.expirationTime)
            }
            .verifyComplete()
    }

    @Test
    fun `validate not existed session failed`() {
        StepVerifier.create(validateSessionProducerModule.Executor(STORED_SESSION_1.id).execute())
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun `validate expired session failed`() {
        redisSessionTemplate.opsForValue()
            .set(STORED_SESSION_1.id, STORED_SESSION_1, Duration.ofSeconds(1)).block()

        Thread.sleep(2000L)
        StepVerifier.create(validateSessionProducerModule.Executor(STORED_SESSION_1.id).execute())
            .expectNextCount(0)
            .verifyComplete()
    }
}
