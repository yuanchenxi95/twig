package com.yuanchenxi95.twig.controllers

import com.google.common.truth.Truth.assertThat
import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.twig.AbstractTestBase
import com.yuanchenxi95.twig.annotations.MockDatabaseConfiguration
import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.constants.RequestMappingValues.Companion.CREATE_TAG
import com.yuanchenxi95.twig.data.INVALID_UUID
import com.yuanchenxi95.twig.data.STORED_SESSION_1
import com.yuanchenxi95.twig.data.STORED_TAG_1
import com.yuanchenxi95.twig.framework.codecs.convertProtobufToJson
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.protobuf.api.CreateTagResponse
import com.yuanchenxi95.twig.protobuf.api.DeleteTagResponse
import com.yuanchenxi95.twig.protobuf.api.createTagRequest
import com.yuanchenxi95.twig.protobuf.api.deleteTagResponse
import com.yuanchenxi95.twig.repositories.TagRepository
import com.yuanchenxi95.twig.utils.getResponse
import com.yuanchenxi95.twig.utils.setUpTagData
import com.yuanchenxi95.twig.utils.setUpTestData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier

@WebFluxTest(
    controllers = [TagController::class],
    excludeAutoConfiguration = [ReactiveUserDetailsServiceAutoConfiguration::class]
)
@MockDatabaseConfiguration
class TagControllerWebClientTests : AbstractTestBase() {
    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var template: R2dbcEntityTemplate

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredSession>

    @BeforeEach
    fun setUp() {
        setUpTestData(template, redisTemplate).block()
    }

    companion object {
        const val TAG_NAME = "FirstTag"
    }

    @Test
    fun `create tag happy case`() {
        val request = createTagRequest {
            name = TAG_NAME
        }

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
            .assertNext {
                assertThat(it.tag.name).isEqualTo(TAG_NAME)
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun `create tag failed without name`() {
        val request = createTagRequest { }
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
            .assertNext {
                assertThat(it.message).isEqualTo("Tag name must not be null or blank.")
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
    }

    @Test
    fun `create tag already existed ignored`() {
        val request = createTagRequest {
            name = TAG_NAME
        }
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
                assertThat(it.tag.name).isEqualTo(TAG_NAME)
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
            .isBadRequest

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun `delete tag should success`() {
        setUpTagData(template).block()

        val responseSpec = client.delete()
            .uri(RequestMappingValues.DELETE_TAG, STORED_TAG_1.id)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectStatus()
            .isOk

        StepVerifier.create(getResponse(responseSpec, DeleteTagResponse.getDefaultInstance()))
            .assertNext {
                assertThat(it).isEqualTo(deleteTagResponse { })
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
    }

    @Test
    fun `delete not existed tag should fail`() {
        setUpTagData(template).block()

        val tagName = INVALID_UUID

        val responseSpec = client.delete()
            .uri(RequestMappingValues.DELETE_TAG, tagName)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectStatus()
            .isBadRequest

        StepVerifier.create(getResponse(responseSpec, TwigApiError.getDefaultInstance()))
            .assertNext {
                assertThat(it.message).isEqualTo("Tag '$INVALID_UUID' not found.")
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(1)
                assertThat(it[0].tagName).isEqualTo(STORED_TAG_1.tagName)
            }
            .verifyComplete()
    }

    @Test
    fun `delete tag from empty repo should fail`() {

        val responseSpec = client.delete()
            .uri(RequestMappingValues.DELETE_TAG, INVALID_UUID)
            .cookies {
                it.add(HttpHeaders.AUTHORIZATION, STORED_SESSION_1.id)
            }
            .exchange()
            .expectStatus()
            .isBadRequest

        StepVerifier.create(getResponse(responseSpec, TwigApiError.getDefaultInstance()))
            .assertNext {
                assertThat(it.message).isEqualTo("Tag '$INVALID_UUID' not found.")
            }
            .verifyComplete()

        StepVerifier.create(tagRepository.findAll().collectList())
            .assertNext {
                assertThat(it.size).isEqualTo(0)
            }
            .verifyComplete()
    }
}
