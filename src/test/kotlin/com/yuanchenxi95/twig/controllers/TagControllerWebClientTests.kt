package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.constants.RequestMappingValues.Companion.CREATE_TAG
import com.yuanchenxi95.twig.data.STORED_SESSION_1
import com.yuanchenxi95.twig.framework.codecs.convertProtobufToJson
import com.yuanchenxi95.twig.producermodules.tags.CreateTagProducerModule
import com.yuanchenxi95.twig.protobuf.api.CreateTagRequest
import com.yuanchenxi95.twig.protobuf.api.CreateTagResponse
import com.yuanchenxi95.twig.repositories.TagRepository
import com.yuanchenxi95.twig.utils.getResponse
import com.yuanchenxi95.twig.utils.setUpTestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@WebFluxTest(
    controllers = [TagController::class],
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class]
)
@MockDatabaseConfiguration
@Import(CreateTagProducerModule::class)
class TagControllerWebClientTests {
    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var tagRepository: TagRepository

    @BeforeEach
    fun setup() {
        setUpTestData(template).block()
    }

    companion object {
        const val TAG_NAME = "FirstTag"
    }

    @Test
    fun `create tag happy case`() {
        val request = CreateTagRequest.newBuilder()
            .setName(TAG_NAME)
            .build()

        val responseSpec = client.post()
            .uri(CREATE_TAG)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(responseSpec, CreateTagResponse.getDefaultInstance()))
            .expectNextMatches {
                it.tag.name == TAG_NAME
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .consumeNextWith {
                Assertions.assertThat(it.size).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun `create tag failed without name`() {
        val request = CreateTagRequest.newBuilder()
            .build()

        val responseSpec = client.post()
            .uri(CREATE_TAG)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isBadRequest

        StepVerifier.create(getResponse(responseSpec, TwigApiError.getDefaultInstance()))
            .expectNextMatches {
                it.message == "Tag name must not be null."
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .consumeNextWith {
                Assertions.assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
    }

    @Test
    fun `create tag already existed ignored`() {
        val request = CreateTagRequest.newBuilder()
            .setName(TAG_NAME)
            .build()

        val responseSpec1 = client.post()
            .uri(CREATE_TAG)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isOk

        val response1 = getResponse(responseSpec1, CreateTagResponse.getDefaultInstance())

        StepVerifier.create(response1)
            .assertNext {
                it.tag.name == TAG_NAME
            }
            .verifyComplete()

        val responseSpec2 = client.post()
            .uri(CREATE_TAG)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertProtobufToJson(request))
            .exchange()
            .expectStatus()
            .isOk

        val response2 = getResponse(responseSpec2, CreateTagResponse.getDefaultInstance())

        StepVerifier.create(Mono.sequenceEqual(response1, response2))
            .expectNext(true)
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .consumeNextWith {
                Assertions.assertThat(it.size).isEqualTo(1)
            }
            .verifyComplete()
    }
}
