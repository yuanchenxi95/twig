package com.yuanchenxi95.twig.utils.databaseutils

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import reactor.core.publisher.Mono

inline fun <reified T> selectList(
    otherCriteria: Criteria,
    criteriaList: Iterable<Criteria>,
    r2dbcEntityTemplate: R2dbcEntityTemplate
): Mono<List<T>> {
    if (criteriaList.none()) {
        return Mono.just(listOf())
    }
    val inCriteria = criteriaList.fold(Criteria.empty()) { acc, criteria -> acc.or(criteria) }

    return r2dbcEntityTemplate.select(
        Query.query(
            otherCriteria.and(inCriteria)
        ),
        T::class.java
    ).collectList()
}

inline fun <reified T> deleteList(
    otherCriteria: Criteria,
    criteriaList: Iterable<Criteria>,
    r2dbcEntityTemplate: R2dbcEntityTemplate
): Mono<Int> {
    if (criteriaList.none()) {
        return Mono.just(0)
    }
    val inCriteria = criteriaList.fold(Criteria.empty()) { acc, criteria -> acc.or(criteria) }

    return r2dbcEntityTemplate.delete(
        Query.query(
            otherCriteria.and(inCriteria)
        ),
        T::class.java
    )
}
