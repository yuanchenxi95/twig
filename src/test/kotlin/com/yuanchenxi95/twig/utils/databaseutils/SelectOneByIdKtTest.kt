package com.yuanchenxi95.twig.utils.databaseutils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Query
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@RunWith(
    MockitoJUnitRunner::class
)
class SelectOneByIdKtTest {

    private val testID = "TEST_ID"

    @Mock
    private var template: R2dbcEntityTemplate = Mockito.mock(R2dbcEntityTemplate::class.java)

    data class TestDataWithCustomizeId(@Id val hello: String, val name: String)

    data class TestDataWithoutId(val hello: String)

    data class TestDataWithDefaultId(val id: String)

    @Test
    fun `findIdKeyField should find the field with Id annotation`() {
        assertEquals(findIdKeyField<TestDataWithCustomizeId>(), "hello")
    }

    @Test
    fun `findIdKeyField should fail when no Id annotation is present`() {
        assertThrows(NullPointerException::class.java) {
            findIdKeyField<TestDataWithoutId>()
        }
    }

    @Test
    fun `selectOneById should select the data with default with the correct query`() {
        val expected = TestDataWithDefaultId(testID)
        val captor = ArgumentCaptor.forClass(Query::class.java)
        Mockito.`when`(
            template.selectOne(
                captor.capture(), any<Class<TestDataWithDefaultId>>()
            )
        )
            .thenReturn(
                Mono.just(expected)
            )

        StepVerifier.create(selectOneById<TestDataWithDefaultId>(testID, template))
            .expectNext(expected)
            .verifyComplete()

        val criteria = captor.value.criteria.get()
        assertEquals(criteria.column.toString(), TestDataWithDefaultId::id.name)
        assertEquals(criteria.value, testID)
    }

    @Test
    fun `selectOneById should select the data with customized Id column with the correct query`() {
        val expected = TestDataWithCustomizeId(testID, "world")
        val captor = ArgumentCaptor.forClass(Query::class.java)
        Mockito.`when`(
            template.selectOne(
                captor.capture(), any<Class<TestDataWithCustomizeId>>()
            )
        )
            .thenReturn(
                Mono.just(expected)
            )

        StepVerifier.create(selectOneById<TestDataWithCustomizeId>(testID, template, false))
            .expectNext(expected)
            .verifyComplete()

        val criteria = captor.value.criteria.get()
        assertEquals(criteria.column.toString(), TestDataWithCustomizeId::hello.name)
        assertEquals(criteria.value, testID)
    }

    @Test
    fun `selectOneById should fail with a data class that does not contain Id annotation`() {
        StepVerifier.create(selectOneById<TestDataWithoutId>(testID, template, false))
            .expectError(NullPointerException::class.java)
            .verify()
    }
}
