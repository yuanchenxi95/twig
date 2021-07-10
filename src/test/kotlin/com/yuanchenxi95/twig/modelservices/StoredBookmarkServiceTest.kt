package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_1
import com.yuanchenxi95.twig.data.STORED_URL_1
import com.yuanchenxi95.twig.models.StoredBookmark
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
class StoredBookmarkServiceTest {
    @Autowired
    private lateinit var template: R2dbcEntityTemplate
    @Autowired
    lateinit var storedBookmarkService: StoredBookmarkService

    @BeforeEach
    fun setUp() {
        setUpTestData(template).block()
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
}
