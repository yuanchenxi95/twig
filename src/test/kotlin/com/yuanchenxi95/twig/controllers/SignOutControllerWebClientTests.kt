package com.yuanchenxi95.twig.controllers

import com.google.common.truth.Truth
import com.google.protobuf.Empty
import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.data.STORED_SESSION_1
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.utils.getResponse
import com.yuanchenxi95.twig.utils.setUpTestData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import java.time.Duration

@WebFluxTest(
    controllers = [SignOutController::class],
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class]
)
@MockDatabaseConfiguration
class SignOutControllerWebClientTests : AbstractTestBase() {
    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `first sign out succeed second sign out failed`() {
        val firstSignOutResponseSpec = client.post()
            .uri(RequestMappingValues.SIGN_OUT)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectCookie()
            .maxAge(HttpHeaders.AUTHORIZATION, Duration.ofSeconds(0))
            .expectCookie()
            .valueEquals(HttpHeaders.AUTHORIZATION, "")
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(firstSignOutResponseSpec, Empty.getDefaultInstance()))
            .assertNext {
                Truth.assertThat(it)
                    .isEqualTo(Empty.newBuilder().build())
            }
            .verifyComplete()

        val secondSignOutResponseSpec = client.post()
            .uri(RequestMappingValues.SIGN_OUT)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectStatus()
            .isUnauthorized

        StepVerifier.create(getResponse(secondSignOutResponseSpec, TwigApiError.getDefaultInstance()))
            .assertNext {
                Truth.assertThat(it.code)
                    .isEqualTo(401)
                Truth.assertThat(it.errorType)
                    .isEqualTo(TwigApiError.ErrorType.AUTHENTICATION_ERROR)
            }
            .verifyComplete()
    }
}
