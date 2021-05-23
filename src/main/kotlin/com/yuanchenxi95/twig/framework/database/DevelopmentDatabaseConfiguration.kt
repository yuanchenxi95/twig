package com.yuanchenxi95.twig.framework.database

import com.yuanchenxi95.twig.application.TwigConfigurations
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
@Profile("development")
class DevelopmentDatabaseConfiguration {
    @Autowired
    lateinit var twigConfigurations: TwigConfigurations

    @Bean
    fun initializer(@Qualifier("connectionFactory") connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)

        initializer.setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
        if (twigConfigurations.enableDatabaseCleanup) {
            initializer.setDatabaseCleaner(ResourceDatabasePopulator(ClassPathResource("cleanup.sql")))
        }
        return initializer
    }
}
