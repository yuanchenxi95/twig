package com.yuanchenxi95.twig.producermodules.bookmarks

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.extensions.proto.ProtoTruth
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.*
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.ListBookmarkResponse
import com.yuanchenxi95.twig.protobuf.api.copy
import com.yuanchenxi95.twig.protobuf.api.listBookmarkResponse
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
import java.time.Instant

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
        )
            .then(template.insert(STORED_BOOKMARK_4))
            .then(template.insert(STORED_BOOKMARK_1))
            .block()

        StepVerifier.create(
            listBookmarkProducerModule.Executor(
                10,
                "",
                TEST_AUTHENTICATION_TOKEN
            ).execute()
        )
            .consumeNextWith {
                assertThat(it).isEqualTo(
                    listBookmarkResponse {
                        bookmarks.add(API_BOOKMARK_1)
                        bookmarks.add(API_BOOKMARK_3)
                        nextPageToken = ""
                    }
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
                10,
                "",
                TEST_AUTHENTICATION_TOKEN
            ).execute()
        )
            .consumeNextWith {
                ProtoTruth.assertThat(it).ignoringRepeatedFieldOrder().isEqualTo(
                    listBookmarkResponse {
                        bookmarks.add(
                            API_BOOKMARK_1.copy {
                                tags.add(STORED_TAG_1.tagName)
                            }
                        )
                        bookmarks.add(
                            API_BOOKMARK_3.copy {
                                tags.add(STORED_TAG_2.tagName)
                                tags.add(STORED_TAG_1.tagName)
                            }
                        )
                        nextPageToken = ""
                    }
                )
            }
            .verifyComplete()
    }

    @Test
    fun `list no bookmark success`() {
        StepVerifier.create(
            listBookmarkProducerModule.Executor(10, "", TEST_AUTHENTICATION_TOKEN).execute()
        )
            .assertNext {
                assertThat(it).isEqualTo(
                    ListBookmarkResponse.getDefaultInstance()
                )
            }.verifyComplete()
    }

    @Test
    fun `list one bookmark without tags success`() {
        val instant = Instant.now()
        parallelExecuteWithLimit(
            listOf(
                template.insert(STORED_URL_1),
                template.insert(STORED_URL_2),
            )
        ).then(
            parallelExecuteWithLimit(
                listOf(
                    template.insert(
                        StoredBookmark(
                            id = "1",
                            urlId = STORED_URL_1.id,
                            displayName = "display name 1",
                            userId = STORED_USER_1.id,
                            createTime = instant
                        )
                    ),
                    template.insert(
                        StoredBookmark(
                            id = "4",
                            urlId = STORED_URL_2.id,
                            displayName = "display name 1",
                            userId = STORED_USER_1.id,
                            createTime = instant
                        )
                    ),
                )
            ).then()
        ).block()

        StepVerifier.create(
            listBookmarkProducerModule.Executor(
                1,
                "",
                TEST_AUTHENTICATION_TOKEN
            ).execute()
        )
            .consumeNextWith {
                assertThat(it.bookmarksList.size).isEqualTo(1)
                assertThat(it.bookmarksList[0]).isEqualTo(API_BOOKMARK_1)
                assertThat(it.nextPageToken).isNotEmpty()
            }
            .verifyComplete()
    }

    @Test
    fun `list one bookmark without tags using pageToken success`() {
        val instant = Instant.now()
        parallelExecuteWithLimit(
            listOf(
                template.insert(STORED_URL_1),
                template.insert(STORED_URL_2),
            )
        ).then(
            parallelExecuteWithLimit(
                listOf(
                    template.insert(
                        StoredBookmark(
                            id = "1",
                            urlId = STORED_URL_1.id,
                            displayName = "display name 1",
                            userId = STORED_USER_1.id,
                            createTime = instant
                        )
                    ),
                    template.insert(
                        StoredBookmark(
                            id = "4",
                            urlId = STORED_URL_2.id,
                            displayName = "display name 1",
                            userId = STORED_USER_1.id,
                            createTime = instant
                        )
                    ),
                )
            ).then()
        ).block()

        StepVerifier.create(
            listBookmarkProducerModule.Executor(
                1,
                "",
                TEST_AUTHENTICATION_TOKEN
            ).execute().flatMap {
                listBookmarkProducerModule.Executor(
                    1,
                    it.nextPageToken,
                    TEST_AUTHENTICATION_TOKEN
                ).execute()
            }
        )
            .consumeNextWith {
                assertThat(it.bookmarksList.size).isEqualTo(1)
                assertThat(it.bookmarksList[0]).isEqualTo(API_BOOKMARK_3)
                assertThat(it.nextPageToken).isEmpty()
            }
            .verifyComplete()
    }
}
