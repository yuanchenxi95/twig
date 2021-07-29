package com.yuanchenxi95.twig.streams

import com.yuanchenxi95.twig.models.StoredUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

@Service
class UrlStreamProducer {

    @Value("\${streams.url.key}")
    private lateinit var streamKey: String

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, StoredUrl>

    fun publishUrlEvent(url: StoredUrl) {
        val record = StreamRecords
            .newRecord()
            .`in`(streamKey)
            .ofObject(url)

        redisTemplate
            .opsForStream<Any, Any>()
            .add(record)
            .subscribe()
    }
}
