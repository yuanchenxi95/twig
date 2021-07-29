package com.yuanchenxi95.twig.streams

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_URL_1
import com.yuanchenxi95.twig.models.StoredUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.test.StepVerifier

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
@Import(
    UrlStreamProducer::class, UrlStreamConsumer::class
)
class UrlStreamProducerAndConsumerTest : AbstractTestBase() {

    @Autowired
    lateinit var urlStreamProducer: UrlStreamProducer

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredUrl>

    @Value("\${streams.url.key}")
    private lateinit var streamKey: String

    @Test
    fun `Url stream produce once`() {

        urlStreamProducer.publishUrlEvent(STORED_URL_1)

        StepVerifier.create(redisTemplate.opsForStream<Any, Any>().size(streamKey))
            .assertNext {
                assertThat(it).isEqualTo(1)
            }
            .verifyComplete()
    }

    fun `Url stream produce and consume once`() {

        TODO("Add test for consumer")
    }
}
