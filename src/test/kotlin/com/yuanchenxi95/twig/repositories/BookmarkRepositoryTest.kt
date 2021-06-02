package com.yuanchenxi95.twig.repositories

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.r2dbc.core.DatabaseClient

@DataR2dbcTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BookmarkRepositoryTest {

    @Autowired
    lateinit var client: DatabaseClient

    @Autowired
    lateinit var bookmarkRepository: BookmarkRepository

    @Test
    fun testDatabaseClientExisted() {
        assertNotNull(client)
    }

    @Test
    fun testPostRepositoryExisted() {
        assertNotNull(bookmarkRepository)
    }
}
