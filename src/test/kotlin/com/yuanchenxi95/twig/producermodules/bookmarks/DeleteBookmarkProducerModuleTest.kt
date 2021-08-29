package com.yuanchenxi95.twig.producermodules.bookmarks

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.INVALID_UUID
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.deleteBookmarkResponse
import com.yuanchenxi95.twig.repositories.BookmarkRepository
import com.yuanchenxi95.twig.repositories.TagsBookmarkRepository
import com.yuanchenxi95.twig.utils.*
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
    DeleteBookmarkProducerModule::class
)
class DeleteBookmarkProducerModuleTest : AbstractTestBase() {
    @Autowired
    private lateinit var deleteBookmarkProducerModule: DeleteBookmarkProducerModule

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var tagBookmarkRepository: TagsBookmarkRepository

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setup() {
        setUpTestData(template, redisTemplate).block()
        setUpUrlData(template).block()
        setUpBookmarkData(template).block()
        setUpTagData(template).block()
        setUpTagBookmarkData(template).block()
    }

    @Test
    fun `delete bookmark`() {
        val bookmarkId = STORED_BOOKMARK_1.id

        StepVerifier.create(bookmarkRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].id).isEqualTo(STORED_BOOKMARK_1.id)
            }
            .verifyComplete()
        StepVerifier.create(tagBookmarkRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].bookmarkId).isEqualTo(STORED_BOOKMARK_1.id)
            }
            .verifyComplete()

        StepVerifier.create(
            deleteBookmarkProducerModule.Executor(bookmarkId, TEST_AUTHENTICATION_TOKEN).execute()
        )
            .assertNext {
                assertThat(it).isEqualTo(deleteBookmarkResponse { })
            }.verifyComplete()

        StepVerifier.create(bookmarkRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
        StepVerifier.create(tagBookmarkRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
    }

    @Test
    fun `delete not existed bookmark throw exception`() {
        val bookmarkId = INVALID_UUID

        StepVerifier.create(
            deleteBookmarkProducerModule.Executor(bookmarkId, TEST_AUTHENTICATION_TOKEN).execute()
        )
            .verifyErrorMessage("Bookmark '$INVALID_UUID' not found.")

        StepVerifier.create(bookmarkRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].id).isEqualTo(STORED_BOOKMARK_1.id)
            }
            .verifyComplete()
    }
}
