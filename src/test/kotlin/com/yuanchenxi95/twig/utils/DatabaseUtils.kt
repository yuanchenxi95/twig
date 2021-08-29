package com.yuanchenxi95.twig.utils

import com.yuanchenxi95.twig.data.*
import com.yuanchenxi95.twig.models.StoredSession
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Mono

fun setUpTestData(
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    reactiveRedisTemplate: ReactiveRedisTemplate<String, StoredSession>
): Mono<Void> {
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

fun setUpBookmarkData(r2dbcEntityTemplate: R2dbcEntityTemplate): Mono<Void> {
    return r2dbcEntityTemplate.insert(STORED_BOOKMARK_1).then()
}

fun setUpTagBookmarkData(r2dbcEntityTemplate: R2dbcEntityTemplate): Mono<Void> {
    return r2dbcEntityTemplate.insert(STORED_TAGS_BOOKMARKS_1).then()
}

fun setUpUrlData(r2dbcEntityTemplate: R2dbcEntityTemplate): Mono<Void> {
    return r2dbcEntityTemplate.insert(STORED_URL_1).then()
}
