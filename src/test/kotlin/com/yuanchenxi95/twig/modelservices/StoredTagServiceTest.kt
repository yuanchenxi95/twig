package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_1
import com.yuanchenxi95.twig.data.STORED_URL_1
import com.yuanchenxi95.twig.data.STORED_USER_1
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.utils.setUpTestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import reactor.test.StepVerifier

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
class StoredTagServiceTest {
    @Autowired
    private lateinit var template: R2dbcEntityTemplate
    @Autowired
    lateinit var storedTagService: StoredTagService
    @Autowired
    lateinit var uuidUtils: UuidUtils

    @BeforeEach
    fun setUp() {
        setUpTestData(template).block()
    }

    @Test
    fun `batchCreateTags success`() {
        val tagNames = listOf("foo", "bar", "baz")

        val expectedStoredTags = tagNames.map {
            StoredTag(
                id = "1",
                tagName = it,
                userId = STORED_USER_1.id
            )
        }
        StepVerifier.create(
            storedTagService.batchCreateTags(STORED_USER_1.id, tagNames)
        )
            .consumeNextWith { createdTags ->
                Assertions.assertThat(createdTags)
                    .usingElementComparatorIgnoringFields(
                        StoredTag::id.name,
                        StoredTag::createTime.name
                    ).containsExactlyInAnyOrderElementsOf(expectedStoredTags)
            }
            .verifyComplete()
    }

    @Test
    fun `batchCreateTags with empty array success`() {
        val tagNames = listOf<String>()

        StepVerifier.create(
            storedTagService.batchCreateTags(STORED_USER_1.id, tagNames)
        )
            .expectNext(listOf())
            .verifyComplete()
    }

    @Test
    fun `batchCreateTags with existing tag should throw error`() {
        storedTagService.batchCreateTags(STORED_USER_1.id, listOf("foo"))
            .block()

        StepVerifier.create(
            storedTagService.batchCreateTags(STORED_USER_1.id, listOf("foo"))
        ).verifyErrorMatches {
            it.message!!.contains("Duplicate entry")
        }
    }

    @Test
    fun `queryTagsForUserByTagNames success`() {
        val tagNames = listOf("foo", "bar")
        storedTagService.batchCreateTags(STORED_USER_1.id, tagNames)
            .block()
        val expectedStoredTags = tagNames.map {
            StoredTag(
                id = "1",
                tagName = it,
                userId = STORED_USER_1.id
            )
        }

        StepVerifier.create(
            storedTagService.queryTagsForUserByTagNames(STORED_USER_1.id, tagNames)
        ).consumeNextWith {
            Assertions.assertThat(it)
                .usingElementComparatorIgnoringFields(
                    StoredTag::id.name,
                    StoredTag::createTime.name
                ).containsExactlyInAnyOrderElementsOf(expectedStoredTags)
        }.verifyComplete()

        StepVerifier.create(
            storedTagService.queryTagsForUserByTagNames(STORED_USER_1.id, listOf(tagNames[0]))
        ).consumeNextWith {
            Assertions.assertThat(it)
                .usingElementComparatorIgnoringFields(
                    StoredTag::id.name,
                    StoredTag::createTime.name
                ).containsExactlyInAnyOrderElementsOf(listOf(expectedStoredTags[0]))
        }.verifyComplete()

        StepVerifier.create(
            storedTagService.queryTagsForUserByTagNames(STORED_USER_1.id, listOf())
        ).expectNext(listOf()).verifyComplete()
    }

    @Test
    fun `queryTagsForUserByTagIds success`() {
        val tagNames = listOf("foo", "bar")
        val storedTags = storedTagService.batchCreateTags(STORED_USER_1.id, tagNames)
            .block()

        val storedTagIds = storedTags!!.map { it.id }

        StepVerifier.create(
            storedTagService.queryTagsForUserByTagIds(STORED_USER_1.id, storedTagIds)
        ).expectNext(storedTags).verifyComplete()

        StepVerifier.create(
            storedTagService.queryTagsForUserByTagIds(STORED_USER_1.id, listOf(storedTagIds[0]))
        ).expectNext(listOf(storedTags[0])).verifyComplete()

        StepVerifier.create(
            storedTagService.queryTagsForUserByTagIds(STORED_USER_1.id, listOf())
        ).expectNext(listOf()).verifyComplete()
    }

    @Test
    fun `queryTagsForBookmark success`() {
        template.insert(STORED_URL_1).block()
        val tagNames = listOf("foo", "bar")
        val storedTags = storedTagService.batchCreateTags(STORED_USER_1.id, tagNames)
            .block()
        template.insert(STORED_BOOKMARK_1).block()

        StepVerifier.create(
            storedTagService.queryTagsForBookmark(STORED_USER_1.id, STORED_BOOKMARK_1.id)
        ).expectNext(listOf()).verifyComplete()

        val fooTag = storedTags!![0]
        template.insert(
            StoredTagsBookmarks(
                id = uuidUtils.generateUUID(),
                bookmarkId = STORED_BOOKMARK_1.id,
                tagId = fooTag.id
            )
        ).block()
        StepVerifier.create(
            storedTagService.queryTagsForBookmark(STORED_USER_1.id, STORED_BOOKMARK_1.id)
        ).expectNext(listOf(fooTag))
            .verifyComplete()
    }
}
