package com.yuanchenxi95.twig.producermodules.tags

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_TAG_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.DeleteTagResponse
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
    DeleteTagProducerModule::class
)
class DeleteTagProducerModuleTest : AbstractTestBase() {
    @Autowired
    private lateinit var deleteTagProducerModule: DeleteTagProducerModule

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setup() {
        setUpTestData(template, redisTemplate).block()
        setUpTagData(template).block()
    }

    @Test
    fun `delete tag`() {
        val tagName = STORED_TAG_1.tagName

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].tagName).isEqualTo(STORED_TAG_1.tagName)
            }
            .verifyComplete()

        StepVerifier.create(
            deleteTagProducerModule.Executor(tagName, TEST_AUTHENTICATION_TOKEN).execute()
        )
            .assertNext {
                assertThat(it).isEqualTo(DeleteTagResponse.newBuilder().build())
            }.verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
    }

    @Test
    fun `delete not existed tag throw exception`() {
        val tagName = STORED_TAG_1.tagName + "NotExisted"

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].tagName).isEqualTo(STORED_TAG_1.tagName)
            }
            .verifyComplete()

        StepVerifier.create(
            deleteTagProducerModule.Executor(tagName, TEST_AUTHENTICATION_TOKEN).execute()
        )
            .verifyErrorMessage("No such Tag.")

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].tagName).isEqualTo(STORED_TAG_1.tagName)
            }
            .verifyComplete()
    }
}
