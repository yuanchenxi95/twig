package com.yuanchenxi95.twig.producermodules.tags

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.API_TAG_1
import com.yuanchenxi95.twig.data.API_TAG_2
import com.yuanchenxi95.twig.data.STORED_TAG_1
import com.yuanchenxi95.twig.data.STORED_TAG_2
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.ListTagResponse
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import com.yuanchenxi95.twig.utils.setUpTestData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.test.StepVerifier

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
@Import(
    ListTagProducerModule::class
)
class ListTagProducerModuleTest : AbstractTestBase() {
    @Autowired
    private lateinit var listTagProducerModule: ListTagProducerModule

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setup() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `list all tags`() {
        template.insert(STORED_TAG_1).then(template.insert(STORED_TAG_2)).block()

        StepVerifier.create(listTagProducerModule.Executor(TEST_AUTHENTICATION_TOKEN).execute())
            .assertNext {
                assertThat(it).isEqualTo(
                    ListTagResponse.newBuilder().addTags(API_TAG_2).addTags(API_TAG_1).build()
                )
            }.verifyComplete()
    }

    @Test
    fun `list empty tag`() {
        StepVerifier.create(listTagProducerModule.Executor(TEST_AUTHENTICATION_TOKEN).execute())
            .assertNext {
                assertThat(it).isEqualTo(
                    ListTagResponse.getDefaultInstance()
                )
            }.verifyComplete()
    }
}
