package com.yuanchenxi95.twig.producermodules.bookmarks

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.extensions.proto.ProtoTruth
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.*
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.ListBookmarkResponse
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
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
@Import(ListBookmarkProducerModule::class, UuidUtils::class)
class ListBookmarkProducerModuleTest : AbstractTestBase() {

    @Autowired
    private lateinit var listBookmarkProducerModule: ListBookmarkProducerModule

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate)
            .block()
    }

    @Test
    fun `list bookmarks without tags success`() {
        parallelExecuteWithLimit(
            listOf(
                template.insert(STORED_URL_1),
                template.insert(STORED_URL_2),
            )
        ).then(
            parallelExecuteWithLimit(
                listOf(
                    template.insert(STORED_BOOKMARK_1),
                    template.insert(STORED_BOOKMARK_4),
                )
            ).then()
        ).block()

        StepVerifier.create(
            listBookmarkProducerModule.Executor(
                TEST_AUTHENTICATION_TOKEN
            ).execute()
        )
            .consumeNextWith {
                assertThat(it).isEqualTo(
                    ListBookmarkResponse.newBuilder().addBookmarks(
                        API_BOOKMARK_1
                    ).addBookmarks(API_BOOKMARK_3).build()
                )
            }
            .verifyComplete()
    }

    @Test
    fun `list all bookmarks with tags success`() {
        parallelExecuteWithLimit(
            listOf(
                template.insert(STORED_URL_1),
                template.insert(STORED_URL_2),
            )
        ).then(
            parallelExecuteWithLimit(
                listOf(
                    template.insert(STORED_BOOKMARK_1),
                    template.insert(STORED_BOOKMARK_4),
                    template.insert(STORED_TAG_1),
                    template.insert(STORED_TAG_2),
                )
            ).then(
                parallelExecuteWithLimit(
                    listOf(
                        template.insert(STORED_TAGS_BOOKMARKS_1),
                        template.insert(STORED_TAGS_BOOKMARKS_2),
                        template.insert(STORED_TAGS_BOOKMARKS_3),
                    )
                ).then()
            )
        ).block()

        StepVerifier.create(
            listBookmarkProducerModule.Executor(
                TEST_AUTHENTICATION_TOKEN
            ).execute()
        )
            .consumeNextWith {
                ProtoTruth.assertThat(it).ignoringRepeatedFieldOrder().isEqualTo(
                    ListBookmarkResponse.newBuilder()
                        .addBookmarks(
                            API_BOOKMARK_1.toBuilder()
                                .addTags(STORED_TAG_1.tagName)
                                .build()
                        ).addBookmarks(
                            API_BOOKMARK_3.toBuilder()
                                .addTags(STORED_TAG_2.tagName)
                                .addTags(STORED_TAG_1.tagName)
                                .build()
                        ).build()
                )
            }
            .verifyComplete()
    }

    @Test
    fun `list no bookmark success`() {
        StepVerifier.create(listBookmarkProducerModule.Executor(TEST_AUTHENTICATION_TOKEN).execute())
            .assertNext {
                assertThat(it).isEqualTo(
                    ListBookmarkResponse.getDefaultInstance()
                )
            }.verifyComplete()
    }
}
