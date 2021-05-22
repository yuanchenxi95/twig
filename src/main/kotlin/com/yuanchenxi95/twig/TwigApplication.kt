package com.yuanchenxi95.twig

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@SpringBootApplication(
    exclude = [
        /**
         *  Remove the default error handler, handles errors using
         *  [com.yuanchenxi95.twig.framework.error_handlers.GlobalErrorHandler].
         */
        ErrorWebFluxAutoConfiguration::class,
    ]
)
class TwigApplication {
    @Bean
    fun initializer(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)

        initializer.setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
//        initializer.setDatabaseCleaner(ResourceDatabasePopulator(ClassPathResource("cleanup.sql")))
        return initializer
    }
}

fun main(args: Array<String>) {
    runApplication<TwigApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
        setBannerMode(Banner.Mode.CONSOLE)
    }
}
