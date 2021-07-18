package com.yuanchenxi95.twig.controllers

import com.google.common.truth.extensions.proto.ProtoTruth
import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.data.*
import com.yuanchenxi95.twig.framework.codecs.convertProtobufToJson
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkResponse
import com.yuanchenxi95.twig.repositories.BookmarkRepository
import com.yuanchenxi95.twig.utils.getResponse
import com.yuanchenxi95.twig.utils.setUpTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier

@WebFluxTest(
    controllers = [BookmarkController::class],
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@MockDatabaseConfiguration
class BookmarkControllerWebClientTests : AbstractTestBase() {
    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    @Test
    fun `create bookmark with invalid url should fail`() {
        val request = CreateBookmarkRequest.newBuilder()
            .setUrl("invalid_url")
            .build()
        val responseSpec = client.post()
            .uri(RequestMappingValues.CREATE_BOOKMARK)
            .cookies {
                it.add(AUTHORIZATION, STORED_SESSION_1.id)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isBadRequest
        StepVerifier.create(getResponse(responseSpec, TwigApiError.getDefaultInstance()))
            .consumeNextWith {
                assertThat(it.message)
                    .isEqualTo("URL 'invalid_url' is not valid.")
            }
            .verifyComplete()
    }

    @Test
    fun `create bookmark with bookmark 1 should success`() {
        val request = CreateBookmarkRequest.newBuilder()
            .setUrl(API_BOOKMARK_1.url)
            .build()
        val responseSpec = client.post()
            .uri(RequestMappingValues.CREATE_BOOKMARK)
            .cookies {
                it.add(AUTHORIZATION, STORED_SESSION_1.id)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(responseSpec, CreateBookmarkResponse.getDefaultInstance()))
            .consumeNextWith {
                ProtoTruth.assertThat(it.bookmark)
                    .ignoringFields(Bookmark.ID_FIELD_NUMBER)
                    .isEqualTo(API_BOOKMARK_1)
            }
            .verifyComplete()

        StepVerifier.create(bookmarkRepository.findAll().collectList())
            .consumeNextWith {
                assertThat(it.size).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun `create bookmark with bookmark 2 should success`() {

        val request = CreateBookmarkRequest.newBuilder()
            .setUrl(API_BOOKMARK_2.url)
            .build()
        val responseSpec = client.post()
            .uri(RequestMappingValues.CREATE_BOOKMARK)
            .cookies {
                it.add(AUTHORIZATION, STORED_SESSION_1.id)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(responseSpec, CreateBookmarkResponse.getDefaultInstance()))
            .consumeNextWith {
                ProtoTruth.assertThat(it.bookmark)
                    .ignoringFields(Bookmark.ID_FIELD_NUMBER)
                    .isEqualTo(API_BOOKMARK_2)
            }
            .verifyComplete()

        StepVerifier.create(bookmarkRepository.findAll().collectList())
            .consumeNextWith {
                assertThat(it.size).isEqualTo(1)
            }
            .verifyComplete()
    }
}
