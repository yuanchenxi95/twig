package com.yuanchenxi95.twig.framework.streams

import com.yuanchenxi95.twig.models.StoredUrl
import io.lettuce.core.RedisBusyException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.RedisSystemException
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions
import org.springframework.data.redis.stream.Subscription
import java.net.InetAddress
import java.time.Duration

@Configuration
class RedisStreamConfig {
    @Value("\${streams.url.key}")
    private lateinit var streamKey: String

    @Value("\${streams.url.group}")
    private lateinit var group: String

    @Autowired
    lateinit var streamListener: StreamListener<String, ObjectRecord<String, StoredUrl>>

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredUrl>

    @Bean
    fun subscription(redisConnectionFactory: RedisConnectionFactory?): Subscription {
        try {
            redisTemplate
                .opsForStream<String, ObjectRecord<String, StoredUrl>>()
                .createGroup(streamKey, group).block()
        } catch (e: RedisSystemException) {
            val cause = e.rootCause
            if (cause != null && RedisBusyException::class.java == cause.javaClass) {
                // TODO("Use logger instead of println")
                println(
                    "STREAM - Redis group already exists, skipping Redis group creation: {}"
                )
            } else throw e
        }

        val options = StreamMessageListenerContainerOptions
            .builder()
            .pollTimeout(Duration.ofSeconds(1))
            .targetType(StoredUrl::class.java)
            .build()
        val listenerContainer = StreamMessageListenerContainer
            .create(redisConnectionFactory, options)
        val subscription = listenerContainer.receiveAutoAck(
            Consumer.from(group, InetAddress.getLocalHost().hostName),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
            streamListener
        )
        listenerContainer.start()
        return subscription
    }
}
