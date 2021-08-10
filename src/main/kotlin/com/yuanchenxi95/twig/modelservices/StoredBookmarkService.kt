package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.models.StoredBookmark
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StoredBookmarkService {
    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    fun selectOneBookmark(userId: String, bookmarkId: String): Mono<StoredBookmark> {
        val userCriteria = getUserCriteria(userId)
        val bookmarkQuery = Criteria.where(StoredBookmark::id.name).`is`(bookmarkId)
        return r2dbcEntityTemplate.selectOne(
            Query.query(userCriteria.and(bookmarkQuery)),
            StoredBookmark::class.java
        )
    }

    fun queryBookmarksForUser(userId: String): Mono<List<StoredBookmark>> {
        return r2dbcEntityTemplate.select(
            Query.query(getUserCriteria(userId)),
            StoredBookmark::class.java
        ).collectList()
    }

    fun getUserCriteria(userId: String): Criteria {
        return Criteria.where(StoredBookmark::userId.name).`is`(userId)
    }
}
