package com.yuanchenxi95.twig.controllers

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.converters.BookmarkConverter
import com.yuanchenxi95.twig.daos.BookmarkRepository
import com.yuanchenxi95.twig.data.API_BOOKMARK_1
import com.yuanchenxi95.twig.data.API_BOOKMARK_2
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_1
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_2
import com.yuanchenxi95.twig.framework.codecs.convertProtobufToJson
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkResponse
import com.yuanchenxi95.twig.protobuf.api.ListBookmarkResponse
import com.yuanchenxi95.twig.utils.getResponse
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@WebFluxTest(
    controllers = [BookmarkController::class],
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class, ReactiveSecurityAutoConfiguration::class]
)
@Import(BookmarkConverter::class)
class BookmarkControllerWebClientTests {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var bookmarkRepository: BookmarkRepository

    @BeforeAll
    fun setUp() {
    }

    @Test
    @WithMockUser
    fun `get all bookmarks`() {
        val bookmarksFlux = Flux.just(
            STORED_BOOKMARK_1, STORED_BOOKMARK_2
        )

        given(bookmarkRepository.findByHostname("example.com"))
            .willReturn(bookmarksFlux)

        val listBookmarksUri = DefaultUriBuilderFactory().builder()
            .path(RequestMappingValues.LIST_BOOKMARK)
            .queryParam("hostname", "example.com")
            .build()

        val expected = ListBookmarkResponse.newBuilder().addAllBookmarks(listOf(API_BOOKMARK_1, API_BOOKMARK_2))
            .build()

        val responseSpec = client.get()
            .uri(listBookmarksUri)
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(responseSpec, ListBookmarkResponse.getDefaultInstance()))
            .consumeNextWith {
                assertThat(it)
                    .ignoringRepeatedFieldOrder()
                    .isEqualTo(expected)
            }
            .verifyComplete()
    }

    @Test
    @WithMockUser
    fun `create bookmark`() {
        given(bookmarkRepository.save(STORED_BOOKMARK_1.copy(id = null)))
            .willReturn(Mono.just(STORED_BOOKMARK_1))

        val request = CreateBookmarkRequest.newBuilder()
            .setBookmark(API_BOOKMARK_1.toBuilder().clearId())
            .build()
        val responseSpec = client.post()
            .uri(RequestMappingValues.CREATE_BOOKMARK)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isOk

        val expected = CreateBookmarkResponse.newBuilder().setBookmark(API_BOOKMARK_1).build()

        StepVerifier.create(getResponse(responseSpec, CreateBookmarkResponse.getDefaultInstance()))
            .consumeNextWith {
                assertThat(it)
                    .isEqualTo(expected)
            }
            .verifyComplete()
    }
}
