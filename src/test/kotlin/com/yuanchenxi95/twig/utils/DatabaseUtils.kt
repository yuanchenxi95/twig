package com.yuanchenxi95.twig.utils

import com.yuanchenxi95.twig.data.STORED_SESSION_1
import com.yuanchenxi95.twig.data.STORED_TAG_1
import com.yuanchenxi95.twig.data.STORED_USER_1
import com.yuanchenxi95.twig.data.STORED_USER_2
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Mono

fun setUpTestData(r2dbcEntityTemplate: R2dbcEntityTemplate, reactiveRedisTemplate: ReactiveRedisTemplate<String, StoredSession>): Mono<Void> {
    return parallelExecuteWithLimit(
        listOf(
            r2dbcEntityTemplate.insert(STORED_USER_1),
            r2dbcEntityTemplate.insert(STORED_USER_2)
        )
    )
        .then(reactiveRedisTemplate.opsForValue().set(STORED_SESSION_1.id, STORED_SESSION_1))
        .then()
}

fun setUpTagData(r2dbcEntityTemplate: R2dbcEntityTemplate): Mono<Void> {
    return r2dbcEntityTemplate.insert(STORED_TAG_1).then()
}
