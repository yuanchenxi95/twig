package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_URL_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.models.StoredUrl
import com.yuanchenxi95.twig.utils.setUpTestData
import org.assertj.core.api.Assertions
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
class StoredUrlServiceTest : AbstractTestBase() {
    @Autowired
    private lateinit var template: R2dbcEntityTemplate
    @Autowired
    lateinit var storedUrlService: StoredUrlService

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `selectOneUrlById success`() {
        template.insert(STORED_URL_1)
            .block()

        StepVerifier.create(
            storedUrlService.selectOneUrlById(STORED_URL_1.id)
        )
            .consumeNextWith {
                Assertions.assertThat(it).usingRecursiveComparison().ignoringFields(
                    StoredUrl::createTime.name,
                    StoredUrl::updateTime.name,
                ).isEqualTo(STORED_URL_1)
            }
            .verifyComplete()
    }

    @Test
    fun `selectOneUrlById empty`() {
        StepVerifier.create(
            storedUrlService.selectOneUrlById(STORED_URL_1.id)
        )
            .expectNextCount(0)
            .verifyComplete()
    }
}
