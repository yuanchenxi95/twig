package com.yuanchenxi95.twig.producermodules.bookmarks

import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import com.google.protobuf.util.FieldMaskUtil
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.*
import com.yuanchenxi95.twig.exceptions.ResourceNotFoundException
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.modelservices.StoredTagService
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.bookmark
import com.yuanchenxi95.twig.protobuf.api.copy
import com.yuanchenxi95.twig.protobuf.api.updateBookmarkRequest
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
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
import org.testcontainers.shaded.com.google.common.collect.ImmutableList
import reactor.test.StepVerifier

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
@Import(UuidUtils::class)
internal class UpdateBookmarkProducerModuleTest : AbstractTestBase() {

    @Autowired
    private lateinit var updateBookmarkProducerModule: UpdateBookmarkProducerModule

    @Autowired
    private lateinit var storedTagService: StoredTagService

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var uuidUtils: UuidUtils

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate)
            .then(
                template.insert(STORED_URL_1)
            )
            .then(
                parallelExecuteWithLimit(
                    listOf(
                        template.insert(STORED_BOOKMARK_1),
                        template.insert(STORED_BOOKMARK_3),
                        template.insert(STORED_TAG_1),
                        template.insert(STORED_TAG_2),
                    )
                ).then()
            )
            .block()
    }

    @Test
    fun `update bookmark success`() {
        val tagsBookmarksId = uuidUtils.generateUUID()
        template.insert(
            StoredTagsBookmarks(
                id = tagsBookmarksId,
                bookmarkId = STORED_BOOKMARK_1.id,
                tagId = STORED_TAG_1.id
            )
        ).block()

        val updateBookmarkRequest = updateBookmarkRequest {
            bookmark = bookmark {
                id = STORED_BOOKMARK_1.id
                tags.addAll(TAGS)
            }
            updateMask = FieldMaskUtil.fromFieldNumbers(
                Bookmark::class.java, Bookmark.TAGS_FIELD_NUMBER
            )
        }

        val updateBookmarkExecutor = updateBookmarkProducerModule.Executor(
            updateBookmarkRequest,
            STORED_BOOKMARK_1.id, TEST_AUTHENTICATION_TOKEN
        )

        StepVerifier.create(updateBookmarkExecutor.execute())
            .consumeNextWith {
                assertThat(it.bookmark)
                    .ignoringRepeatedFieldOrder()
                    .isEqualTo(
                        API_BOOKMARK_1.copy {
                            id = STORED_BOOKMARK_1.id
                            tags.addAll(TAGS)
                        }
                    )
            }.verifyComplete()

        // Verifies the tags are created in the database
        StepVerifier.create(
            storedTagService.queryTagsForBookmark(
                userId = STORED_USER_1.id,
                bookmarkId = STORED_BOOKMARK_1.id,
            )
        ).consumeNextWith { tagsOfBookmark1 ->
            assertThat(
                tagsOfBookmark1.map {
                    it.tagName
                }
            ).containsExactlyInAnyOrder(*TAGS.toTypedArray())
        }
    }

    @Test
    fun `update bookmark to remove all the tags should work`() {
        val tagsBookmarksId = uuidUtils.generateUUID()
        template.insert(
            StoredTagsBookmarks(
                id = tagsBookmarksId,
                bookmarkId = STORED_BOOKMARK_1.id,
                tagId = STORED_TAG_1.id
            )
        ).block()

        val updateBookmarkRequest = updateBookmarkRequest {
            bookmark = bookmark { id = STORED_BOOKMARK_1.id }
            updateMask = FieldMaskUtil.fromFieldNumbers(
                Bookmark::class.java, Bookmark.TAGS_FIELD_NUMBER
            )
        }

        val updateBookmarkExecutor = updateBookmarkProducerModule.Executor(
            updateBookmarkRequest,
            STORED_BOOKMARK_1.id, TEST_AUTHENTICATION_TOKEN
        )

        StepVerifier.create(updateBookmarkExecutor.execute())
            .consumeNextWith {
                assertThat(it.bookmark)
                    .ignoringRepeatedFieldOrder()
                    .isEqualTo(
                        API_BOOKMARK_1
                    )
            }.verifyComplete()

        // Verifies the tags are created in the database
        StepVerifier.create(
            storedTagService.queryTagsForBookmark(
                userId = STORED_USER_1.id,
                bookmarkId = STORED_BOOKMARK_1.id,
            )
        ).consumeNextWith { tagsOfBookmark1 ->
            assertThat(tagsOfBookmark1).isEmpty()
        }
    }

    @Test
    fun `updateBookmark user does not match failed`() {
        val updateBookmarkRequest = updateBookmarkRequest {
            bookmark = bookmark {
                id = STORED_BOOKMARK_3.id
                tags.addAll(TAGS)
            }
            updateMask = FieldMaskUtil.fromFieldNumbers(
                Bookmark::class.java, Bookmark.TAGS_FIELD_NUMBER
            )
        }
        val updateBookmarkExecutor = updateBookmarkProducerModule.Executor(
            updateBookmarkRequest,
            STORED_BOOKMARK_3.id, TEST_AUTHENTICATION_TOKEN
        )

        StepVerifier.create(updateBookmarkExecutor.execute())
            .consumeErrorWith {
                assertThat(it).isInstanceOf(ResourceNotFoundException::class.java)
                assertThat(it).hasMessage("Resource bookmark with ID '3' not found.")
            }.verify()
    }

    companion object {
        private val TAGS = ImmutableList.of("foo", "bar", STORED_TAG_2.tagName)
    }
}
