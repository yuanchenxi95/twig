package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.models.StoredSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StoredSessionService {

    @Autowired
    lateinit var redisSessionTemplate: ReactiveRedisTemplate<String, StoredSession>

    fun deleteSession(sessionId: String): Mono<Boolean> {
        return redisSessionTemplate.opsForValue().delete(sessionId)
    }
}
