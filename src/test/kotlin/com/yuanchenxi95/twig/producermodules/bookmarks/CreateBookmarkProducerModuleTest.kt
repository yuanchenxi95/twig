package com.yuanchenxi95.twig.producermodules.bookmarks

import com.google.common.truth.extensions.proto.ProtoTruth
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.API_BOOKMARK_1
import com.yuanchenxi95.twig.data.STORED_URL_1
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.models.StoredUrl
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import com.yuanchenxi95.twig.repositories.UrlRepository
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import com.yuanchenxi95.twig.utils.setUpTestData
import org.assertj.core.api.Assertions.assertThat
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
@Import(CreateBookmarkProducerModule::class, UuidUtils::class)
internal class CreateBookmarkProducerModuleTest : AbstractTestBase() {

    @Autowired
    private lateinit var createBookmarkProducerModule: CreateBookmarkProducerModule

    @Autowired
    private lateinit var urlRepository: UrlRepository

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `create bookmark`() {
        val request = CreateBookmarkRequest.newBuilder()
            .setUrl(API_BOOKMARK_1.url)
            .build()
        StepVerifier.create(
            createBookmarkProducerModule.Executor(
                request,
                TEST_AUTHENTICATION_TOKEN
            ).execute()
        )
            .consumeNextWith {
                ProtoTruth.assertThat(it.bookmark)
                    .ignoringFields(Bookmark.ID_FIELD_NUMBER)
                    .isEqualTo(API_BOOKMARK_1)
            }
            .verifyComplete()

        StepVerifier.create(urlRepository.findAll().collectList())
            .consumeNextWith {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0])
                    .usingRecursiveComparison().ignoringFields(
                        StoredUrl::id.name,
                        StoredUrl::createTime.name,
                        StoredUrl::updateTime.name,
                    ).isEqualTo(STORED_URL_1)
            }
            .verifyComplete()
    }
}
