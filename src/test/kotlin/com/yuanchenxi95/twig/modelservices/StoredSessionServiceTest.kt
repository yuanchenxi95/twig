package com.yuanchenxi95.twig.modelservices

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_SESSION_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.utils.setUpTestData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.test.StepVerifier

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
class StoredSessionServiceTest : AbstractTestBase() {
    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    lateinit var storedSessionService: StoredSessionService

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `delete session success`() {
        StepVerifier.create(redisTemplate.opsForValue().get(STORED_SESSION_1.id))
            .assertNext {
                assertThat(it).isEqualTo(STORED_SESSION_1)
            }
            .verifyComplete()

        StepVerifier.create(storedSessionService.deleteSession(STORED_SESSION_1.id))
            .assertNext {
                assertThat(it).isTrue()
            }
            .verifyComplete()

        StepVerifier.create(redisTemplate.opsForValue().get(STORED_SESSION_1.id))
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun `delete session no such session id`() {
        StepVerifier.create(redisTemplate.opsForValue().get(STORED_SESSION_1.id))
            .assertNext {
                assertThat(it).isEqualTo(STORED_SESSION_1)
            }
            .verifyComplete()

        StepVerifier.create(storedSessionService.deleteSession(STORED_SESSION_1.id + "no such id"))
            .assertNext {
                assertThat(it).isFalse()
            }
            .verifyComplete()

        StepVerifier.create(redisTemplate.opsForValue().get(STORED_SESSION_1.id))
            .assertNext {
                assertThat(it).isEqualTo(STORED_SESSION_1)
            }
            .verifyComplete()
    }
}
