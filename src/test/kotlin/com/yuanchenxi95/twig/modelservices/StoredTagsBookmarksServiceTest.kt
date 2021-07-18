package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_1
import com.yuanchenxi95.twig.data.STORED_TAG_1
import com.yuanchenxi95.twig.data.STORED_TAG_2
import com.yuanchenxi95.twig.data.STORED_URL_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
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
class StoredTagsBookmarksServiceTest : AbstractTestBase() {
    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    lateinit var storedTagsBookmarkService: StoredTagsBookmarksService

    @Autowired
    lateinit var storedTagService: StoredTagService

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()

        template.insert(STORED_URL_1).then(
            parallelExecuteWithLimit(
                listOf(
                    template.insert(STORED_BOOKMARK_1),
                    template.insert(STORED_TAG_1),
                    template.insert(STORED_TAG_2)
                )
            ).then()
        ).block()
    }

    @Test
    fun `batchCreateReferences success`() {

        val tags = listOf(STORED_TAG_1.id, STORED_TAG_2.id)

        StepVerifier.create(
            storedTagsBookmarkService.batchCreateReferences(
                STORED_BOOKMARK_1.id,
                tags
            )
        )
            .consumeNextWith { storedTagsBookmarks ->
                Assertions.assertThat(storedTagsBookmarks).usingRecursiveComparison()
                    .ignoringFields(
                        StoredTagsBookmarks::id.name,
                        StoredTagsBookmarks::createTime.name,
                    ).isEqualTo(
                        tags.map {
                            StoredTagsBookmarks(
                                id = "1", bookmarkId = STORED_BOOKMARK_1.id,
                                tagId = it
                            )
                        }
                    )
            }
            .verifyComplete()
    }

    @Test
    fun `batchDeleteReferences success`() {

        val tags = listOf(STORED_TAG_1.id, STORED_TAG_2.id)
        storedTagsBookmarkService.batchCreateReferences(
            STORED_BOOKMARK_1.id,
            tags
        ).block()

        StepVerifier.create(
            storedTagsBookmarkService.batchDeleteReferences(
                STORED_BOOKMARK_1.id,
                tags
            )
        )
            .consumeNextWith { referencesDeleted ->
                Assertions.assertThat(referencesDeleted).isEqualTo(2)
            }
            .verifyComplete()

        StepVerifier.create(
            storedTagService.queryTagsForBookmark(STORED_BOOKMARK_1.id, STORED_BOOKMARK_1.userId)
        ).expectNext(listOf())
            .verifyComplete()
    }
}
