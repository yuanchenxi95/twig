package com.yuanchenxi95.twig.annotations

import com.yuanchenxi95.twig.producermodules.users.LoginUserProducerModule
import com.yuanchenxi95.twig.producermodules.users.ValidateSessionProducerModule
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@OverrideAutoConfiguration(enabled = false)
@AutoConfigureDataR2dbc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Import(LoginUserProducerModule::class, ValidateSessionProducerModule::class)
/** Mark the context as dirty after each test method. */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
annotation class MockDatabaseConfiguration
