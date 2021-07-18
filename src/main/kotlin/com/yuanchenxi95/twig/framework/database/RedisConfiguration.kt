package com.yuanchenxi95.twig.framework.database

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.yuanchenxi95.twig.models.StoredSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import javax.annotation.PreDestroy

@Configuration
class RedisConfiguration {
    @Autowired
    lateinit var factory: RedisConnectionFactory

    @Bean
    fun reactiveRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory?
    ): ReactiveRedisTemplate<String, StoredSession>? {
        val serializer: Jackson2JsonRedisSerializer<StoredSession> =
            Jackson2JsonRedisSerializer(StoredSession::class.java)
        serializer.setObjectMapper(jacksonObjectMapper().registerModule(JavaTimeModule()))

        val builder =
            RedisSerializationContext.newSerializationContext<String, StoredSession>(StringRedisSerializer())

        val context = builder.value(serializer).build()

        return ReactiveRedisTemplate(reactiveRedisConnectionFactory!!, context)
    }

    @PreDestroy
    fun cleanRedis() {
        factory.connection
            .flushDb()
    }
}
