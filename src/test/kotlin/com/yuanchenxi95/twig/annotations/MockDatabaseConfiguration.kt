package com.yuanchenxi95.twig.annotations

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.annotation.DirtiesContext

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@OverrideAutoConfiguration(enabled = false)
@AutoConfigureDataR2dbc
@AutoConfigureDataRedis
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ComponentScan("com.yuanchenxi95.twig.producermodules", "com.yuanchenxi95.twig.modelservices")
/** Mark the context as dirty after each test method. */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
annotation class MockDatabaseConfiguration
