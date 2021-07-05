package com.yuanchenxi95.twig.utils

import com.yuanchenxi95.twig.data.STORED_SESSION_1
import com.yuanchenxi95.twig.data.STORED_TAG_1
import com.yuanchenxi95.twig.data.STORED_USER_1
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import reactor.core.publisher.Mono

fun setUpTestData(r2dbcEntityTemplate: R2dbcEntityTemplate): Mono<Void> {
    return r2dbcEntityTemplate.insert(STORED_USER_1)
        .then(r2dbcEntityTemplate.insert(STORED_SESSION_1))
        .then()
}

fun setUpTagData(r2dbcEntityTemplate: R2dbcEntityTemplate): Mono<Void> {
    return r2dbcEntityTemplate.insert(STORED_TAG_1).then()
}
