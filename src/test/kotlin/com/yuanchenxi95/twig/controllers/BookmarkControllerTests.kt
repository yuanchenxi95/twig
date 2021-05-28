package com.yuanchenxi95.twig.controllers

import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import com.yuanchenxi95.twig.converters.BookmarkConverter
import com.yuanchenxi95.twig.daos.BookmarkRepository
import com.yuanchenxi95.twig.data.API_BOOKMARK_1
import com.yuanchenxi95.twig.data.API_BOOKMARK_2
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_1
import com.yuanchenxi95.twig.data.STORED_BOOKMARK_2
import com.yuanchenxi95.twig.protobuf.api.ListBookmarkResponse
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@SpringBootTest
@Import(BookmarkConverter::class)
class BookmarkControllerTests {

    @Autowired
    private lateinit var bookmarkController: BookmarkController

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

        val expected = ListBookmarkResponse.newBuilder().addAllBookmarks(listOf(API_BOOKMARK_1, API_BOOKMARK_2))
            .build()

        val bookmarks = bookmarkController.listBookmarks("example.com")
        StepVerifier.create(bookmarks)
            .consumeNextWith {
                assertThat(it).ignoringRepeatedFieldOrder()
                    .isEqualTo(expected)
            }
            .verifyComplete()
    }
}
