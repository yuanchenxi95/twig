package com.yuanchenxi95.twig.producermodules.users

import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_USER_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.producermodules.tags.CreateTagProducerModule
import com.yuanchenxi95.twig.protobuf.api.getUserInformationResponse
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import com.yuanchenxi95.twig.utils.protobufutils.convertInstantToTimestamp
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
internal class GetUserInformationProducerModuleTest : AbstractTestBase() {

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var getUserInformationProducerModule: GetUserInformationProducerModule

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `get login user information`() {
        val getUserInformationResponseExecutor =
            getUserInformationProducerModule.Executor(TEST_AUTHENTICATION_TOKEN)

        StepVerifier.create(getUserInformationResponseExecutor.execute())
            .consumeNextWith {
                assertThat(it).isEqualTo(
                    getUserInformationResponse {
                        id = STORED_USER_1.id
                        name = STORED_USER_1.name
                        email = STORED_USER_1.userEmail
                        expirationTime =
                            convertInstantToTimestamp(TEST_AUTHENTICATION_TOKEN.getExpirationTime())
                    }
                )
            }
            .verifyComplete()
    }
}
