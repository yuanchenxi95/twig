package com.yuanchenxi95.twig.framework.database

import com.yuanchenxi95.twig.application.TwigConfigurations
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableR2dbcAuditing
class DatabaseConfiguration {
    @Autowired
    lateinit var twigConfigurations: TwigConfigurations

    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)

        if (twigConfigurations.enableDatabaseSetup) {
            initializer.setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
            initializer.setDatabaseCleaner(ResourceDatabasePopulator(ClassPathResource("cleanup.sql")))
        }
        return initializer
    }
}
