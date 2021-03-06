package com.yuanchenxi95.twig.producermodules.tags

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_TAG_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.createTagRequest
import com.yuanchenxi95.twig.repositories.TagRepository
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import com.yuanchenxi95.twig.utils.setUpTagData
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
@Import(
    CreateTagProducerModule::class
)
class CreateTagProducerModuleTest : AbstractTestBase() {

    @Autowired
    private lateinit var createTagProducerModule: CreateTagProducerModule

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    companion object {
        const val TAG_NAME = "FirstTag"
    }

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `create tag`() {
        val request = createTagRequest {
            name = TAG_NAME
        }

        StepVerifier.create(
            createTagProducerModule.Executor(request, TEST_AUTHENTICATION_TOKEN).execute()
        )
            .assertNext {
                assertThat(it.tag.name).isEqualTo(TAG_NAME)
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].tagName).isEqualTo(TAG_NAME)
            }
            .verifyComplete()
    }

    @Test
    fun `create tag should fail with duplicate tags`() {
        setUpTagData(template).block()
        val request = createTagRequest {
            name = STORED_TAG_1.tagName
        }

        StepVerifier.create(
            createTagProducerModule.Executor(request, TEST_AUTHENTICATION_TOKEN).execute()
        )
            .verifyErrorMessage("Tag with name '${STORED_TAG_1.tagName}' already exists.")
    }
}
