package com.yuanchenxi95.twig.controllers

import com.google.common.truth.Truth
import com.google.common.truth.extensions.proto.ProtoTruth
import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.data.*
import com.yuanchenxi95.twig.framework.codecs.convertProtobufToJson
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.*
import com.yuanchenxi95.twig.repositories.BookmarkRepository
import com.yuanchenxi95.twig.utils.getResponse
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
import com.yuanchenxi95.twig.utils.setUpBookmarkData
import com.yuanchenxi95.twig.utils.setUpTestData
import com.yuanchenxi95.twig.utils.setUpUrlData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpHeaders
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
        val request = createBookmarkRequest {
            bookmark = bookmark {
                url = "invalid_url"
            }
        }
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
        val request = createBookmarkRequest {
            bookmark = API_BOOKMARK_1.copy {
                clearId()
            }
        }
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
        val request = createBookmarkRequest {
            bookmark = API_BOOKMARK_2.copy { clearId() }
        }

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

    @Test
    fun `list bookmark with bookmark 1 success`() {
        parallelExecuteWithLimit(
            listOf(
                template.insert(STORED_URL_1)
            )
        ).then(
            parallelExecuteWithLimit(
                listOf(
                    template.insert(STORED_BOOKMARK_1),
                    template.insert(STORED_TAG_1)
                )
            ).then(
                parallelExecuteWithLimit(
                    listOf(
                        template.insert(STORED_TAGS_BOOKMARKS_1),
                    )
                ).then()
            )
        ).block()

        val responseSpec = client.get()
            .uri(RequestMappingValues.LIST_BOOKMARK)
            .cookies {
                it.add(AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(responseSpec, ListBookmarkResponse.getDefaultInstance()))
            .consumeNextWith {
                ProtoTruth.assertThat(it.bookmarksList)
                    .isEqualTo(
                        listOf(
                            API_BOOKMARK_1.copy {
                                tags.add(STORED_TAG_1.tagName)
                            }
                        )
                    )
                assertThat(it.nextPageToken).isEmpty()
            }
            .verifyComplete()
    }

    @Test
    fun `list bookmarks using pageToken success`() {
        parallelExecuteWithLimit(
            listOf(
                template.insert(STORED_URL_1),
                template.insert(STORED_URL_2)
            )
        )
            .then(template.insert(STORED_BOOKMARK_4))
            .then(template.insert(STORED_BOOKMARK_1))
            .then(template.insert(STORED_TAG_1))
            .then(
                parallelExecuteWithLimit(
                    listOf(
                        template.insert(STORED_TAGS_BOOKMARKS_1),
                        template.insert(STORED_TAGS_BOOKMARKS_2),
                    )
                ).then()
            )
            .block()

        val responseSpec = client.get()
            .uri { uriBuilder ->
                uriBuilder.path(RequestMappingValues.LIST_BOOKMARK)
                    .queryParam("page_size", "1").build()
            }
            .cookies {
                it.add(AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(
            getResponse(
                responseSpec,
                ListBookmarkResponse.getDefaultInstance()
            )
                .flatMap {
                    val responseSpec2 = client.get()
                        .uri { uriBuilder ->
                            uriBuilder.path(RequestMappingValues.LIST_BOOKMARK)
                                .queryParam("page_size", "5")
                                .queryParam("page_token", it.nextPageToken).build()
                        }
                        .cookies {
                            it.add(AUTHORIZATION, STORED_SESSION_1.id)
                        }
                        .exchange()
                        .expectStatus()
                        .isOk

                    getResponse(
                        responseSpec2,
                        ListBookmarkResponse.getDefaultInstance()
                    )
                }
        )
            .consumeNextWith {
                ProtoTruth.assertThat(it.bookmarksList)
                    .isEqualTo(
                        listOf(
                            API_BOOKMARK_3.copy {
                                tags.add(STORED_TAG_1.tagName)
                            }
                        )
                    )
                assertThat(it.nextPageToken).isEmpty()
            }
            .verifyComplete()
    }

    @Test
    fun `delete bookmark should success`() {
        setUpUrlData(template).block()
        setUpBookmarkData(template).block()

        val responseSpec = client.delete()
            .uri(RequestMappingValues.DELETE_BOOKMARK, STORED_BOOKMARK_1.id)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(responseSpec, DeleteBookmarkResponse.getDefaultInstance()))
            .assertNext {
                Truth.assertThat(it).isEqualTo(deleteBookmarkResponse { })
            }
            .verifyComplete()

        StepVerifier.create(bookmarkRepository.findAll().collectList())
            .assertNext {
                Truth.assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
    }
}
