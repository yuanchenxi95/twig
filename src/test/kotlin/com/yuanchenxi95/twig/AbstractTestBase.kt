package com.yuanchenxi95.twig

import org.junit.runner.RunWith
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.GenericContainer

@RunWith(SpringRunner::class)
@ContextConfiguration(initializers = [AbstractTestBase.Initializer::class])
abstract class AbstractTestBase {
    companion object {
        val redisContainer = object : GenericContainer<Nothing>("redis:alpine") {
            init {
                withExposedPorts(6379)
            }
        }
    }

    internal class Initializer :
        ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            redisContainer.start()

            TestPropertyValues.of(
                "spring.redis.host=${redisContainer.containerIpAddress}",
                "spring.redis.port=${redisContainer.firstMappedPort}"
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}
