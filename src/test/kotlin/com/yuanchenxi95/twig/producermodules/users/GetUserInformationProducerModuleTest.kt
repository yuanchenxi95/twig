package com.yuanchenxi95.twig.producermodules.users

import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.data.STORED_USER_1
import com.yuanchenxi95.twig.producermodules.tags.CreateTagProducerModule
import com.yuanchenxi95.twig.protobuf.api.GetUserInformationResponse
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import com.yuanchenxi95.twig.utils.protobufutils.convertInstantToTimestamp
import com.yuanchenxi95.twig.utils.setUpTestData
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import reactor.test.StepVerifier

@WebFluxTest(
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
@Import(
    CreateTagProducerModule::class
)
internal class GetUserInformationProducerModuleTest {

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var getUserInformationProducerModule: GetUserInformationProducerModule

    @BeforeEach
    fun setup() {
        setUpTestData(template).block()
    }

    @Test
    fun `get login user information`() {
        val getUserInformationResponseExecutor = getUserInformationProducerModule.Executor(TEST_AUTHENTICATION_TOKEN)

        StepVerifier.create(getUserInformationResponseExecutor.execute())
            .consumeNextWith {
                assertThat(it).isEqualTo(
                    GetUserInformationResponse.newBuilder()
                        .setId(STORED_USER_1.id)
                        .setName(STORED_USER_1.name)
                        .setEmail(STORED_USER_1.userEmail)
                        .setExpirationTime(convertInstantToTimestamp(TEST_AUTHENTICATION_TOKEN.getExpirationTime()))
                        .build()
                )
            }
            .verifyComplete()
    }
}
