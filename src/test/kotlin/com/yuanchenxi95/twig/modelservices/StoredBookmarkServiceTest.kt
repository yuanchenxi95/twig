package com.yuanchenxi95.twig.modelservices

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_1
import com.yuanchenxi95.twig.data.STORED_URL_1
import com.yuanchenxi95.twig.data.STORED_USER_1
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredSession
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
import java.time.Instant

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
class StoredBookmarkServiceTest : AbstractTestBase() {
    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    lateinit var storedBookmarkService: StoredBookmarkService

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `selectOneBookmark success`() {
        template.insert(STORED_URL_1)
            .then(template.insert(STORED_BOOKMARK_1))
            .block()

        StepVerifier.create(
            storedBookmarkService.selectOneBookmark(
                STORED_BOOKMARK_1.userId,
                STORED_BOOKMARK_1.urlId
            )
        )
            .consumeNextWith {
                Assertions.assertThat(it).usingRecursiveComparison().ignoringFields(
                    StoredBookmark::createTime.name,
                    StoredBookmark::updateTime.name,
                ).isEqualTo(STORED_BOOKMARK_1)
            }
            .verifyComplete()
    }

    @Test
    fun `selectOneBookmark empty`() {
        StepVerifier.create(
            storedBookmarkService.selectOneBookmark(
                STORED_BOOKMARK_1.userId,
                STORED_BOOKMARK_1.urlId
            )
        )
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun `queryBookmarksForUserOrderByCreateTime with same createTime success`() {
        `insert bookmark with same createTime`()

        StepVerifier.create(
            storedBookmarkService.queryBookmarksForUserOrderByCreateTime(
                STORED_BOOKMARK_1.userId,
                4
            )
        )
            .assertNext {
                assertThat(it.size).isEqualTo(4)
                for (i in 1..4) {
                    assertThat(it[i - 1].id).isEqualTo(i.toString())
                    assertThat(it[i - 1].urlId).isEqualTo(STORED_URL_1.id)
                }
            }
            .verifyComplete()
    }

    @Test
    fun `queryBookmarksForUserOrderByCreateTime with different createTime success`() {
        `insert bookmark with different createTime`()

        StepVerifier.create(
            storedBookmarkService.queryBookmarksForUserOrderByCreateTime(
                STORED_BOOKMARK_1.userId,
                4
            )
        )
            .assertNext {
                assertThat(it.size).isEqualTo(4)
                for (i in 1..4) {
                    assertThat(it[i - 1].id).isEqualTo(i.toString())
                    assertThat(it[i - 1].urlId).isEqualTo(STORED_URL_1.id)
                }
            }
            .verifyComplete()
    }

    @Test
    fun `queryBookmarksForUserByLastIdAndLastCreateTime with different createTime success`() {
        `insert bookmark with different createTime`()

        StepVerifier.create(
            storedBookmarkService.queryBookmarksForUserOrderByCreateTime(
                STORED_BOOKMARK_1.userId,
                4
            ).flatMap {
                storedBookmarkService.queryBookmarksForUserByLastIdAndLastCreateTime(
                    STORED_BOOKMARK_1.userId,
                    4,
                    it.last().createTime!!,
                    it.last().id
                )
            }
        )
            .assertNext {
                assertThat(it.size).isEqualTo(4)
                for (i in 5..8) {
                    assertThat(it[i - 5].id).isEqualTo(i.toString())
                    assertThat(it[i - 5].urlId).isEqualTo(STORED_URL_1.id)
                }
            }
            .verifyComplete()
    }

    @Test
    fun `queryBookmarksForUserByLastIdAndLastCreateTime with same createTime success`() {
        `insert bookmark with same createTime`()

        StepVerifier.create(
            storedBookmarkService.queryBookmarksForUserOrderByCreateTime(
                STORED_BOOKMARK_1.userId,
                4
            ).flatMap {
                storedBookmarkService.queryBookmarksForUserByLastIdAndLastCreateTime(
                    STORED_BOOKMARK_1.userId,
                    4,
                    it.last().createTime!!,
                    it.last().id
                )
            }
        ).assertNext {
            assertThat(it.size).isEqualTo(4)
            for (i in 5..8) {
                assertThat(it[i - 5].id).isEqualTo(i.toString())
                assertThat(it[i - 5].urlId).isEqualTo(STORED_URL_1.id)
            }
        }
            .verifyComplete()
    }

    private fun `insert bookmark with same createTime`() {
        template.insert(STORED_URL_1).block()
        var instant = Instant.now()
        for (id in 9 downTo 1) {
            val storedBookmark = StoredBookmark(id = id.toString(), urlId = STORED_URL_1.id, displayName = "display name 1", userId = STORED_USER_1.id, createTime = instant)
            template.insert(storedBookmark).block()
        }
    }

    private fun `insert bookmark with different createTime`() {
        template.insert(STORED_URL_1).block()
        var instant = Instant.now()
        for (id in 9 downTo 1) {
            instant = instant.plusMillis(2000)
            val storedBookmark = StoredBookmark(id = id.toString(), urlId = STORED_URL_1.id, displayName = "display name 1", userId = STORED_USER_1.id, createTime = instant)
            template.insert(storedBookmark).block()
        }
    }
}
