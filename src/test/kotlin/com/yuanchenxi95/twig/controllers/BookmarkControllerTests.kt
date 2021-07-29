package com.yuanchenxi95.twig.controllers

import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.data.API_BOOKMARK_1
import com.yuanchenxi95.twig.producermodules.bookmarks.CreateBookmarkProducerModule
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkRequest
import com.yuanchenxi95.twig.protobuf.api.CreateBookmarkResponse
import com.yuanchenxi95.twig.utils.TEST_AUTHENTICATION_TOKEN
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookmarkControllerTests : AbstractTestBase() {

    @MockBean
    private lateinit var createBookmarkProducerModule: CreateBookmarkProducerModule

    @Autowired
    private lateinit var bookmarkController: BookmarkController

    @BeforeAll
    fun setUp() {
    }

    @Test
    @WithMockUser
    fun `create bookmark`() {

        val request = CreateBookmarkRequest.newBuilder().setUrl(API_BOOKMARK_1.url).build()

        val expected = CreateBookmarkResponse.newBuilder().setBookmark(API_BOOKMARK_1).build()
        given(createBookmarkProducerModule.execute(request, TEST_AUTHENTICATION_TOKEN))
            .willReturn(
                Mono.just(expected)
            )

        val createBookmarkRequest = bookmarkController.createBookmark(request, TEST_AUTHENTICATION_TOKEN)
        StepVerifier.create(createBookmarkRequest)
            .consumeNextWith {
                assertThat(it).ignoringRepeatedFieldOrder()
                    .isEqualTo(expected)
            }
            .verifyComplete()
    }
}
