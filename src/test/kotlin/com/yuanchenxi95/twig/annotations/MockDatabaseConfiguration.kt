package com.yuanchenxi95.twig.annotations

import com.yuanchenxi95.twig.converters.BookmarkConverter
import com.yuanchenxi95.twig.producermodules.bookmarks.CreateBookmarkProducerModule
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.test.annotation.DirtiesContext

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@OverrideAutoConfiguration(enabled = false)
@AutoConfigureDataR2dbc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Import(BookmarkConverter::class, CreateBookmarkProducerModule::class)
/** Mark the context as dirty after each test method. */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
annotation class MockDatabaseConfiguration
