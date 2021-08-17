package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.models.StoredBookmark
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant

@Component
class StoredBookmarkService {
    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    fun selectOneBookmark(
        userId: String,
        bookmarkId: String
    ): Mono<StoredBookmark> {
        val userCriteria = getUserCriteria(userId)
        val bookmarkQuery =
            Criteria.where(StoredBookmark::id.name).`is`(bookmarkId)
        return r2dbcEntityTemplate.selectOne(
            Query.query(userCriteria.and(bookmarkQuery)),
            StoredBookmark::class.java
        )
    }

    fun queryBookmarksForUserOrderByCreateTime(
        userId: String,
        pageSize: Int
    ): Mono<List<StoredBookmark>> {
        return r2dbcEntityTemplate.select(
            Query.query(getUserCriteria(userId)).sort(
                Sort.by(
                    listOf(
                        Sort.Order(
                            Sort.Direction.DESC,
                            StoredBookmark::createTime.name
                        ),
                        Sort.Order(Sort.Direction.ASC, StoredBookmark::id.name)
                    )
                )
            ).limit(pageSize),
            StoredBookmark::class.java
        ).collectList()
    }

    fun queryBookmarksForUserByLastIdAndLastCreateTime(
        userId: String,
        pageSize: Int,
        lastCreateTime: Instant,
        lastId: String
    ): Mono<List<StoredBookmark>> {
        val createTimeEqualCriteria =
            Criteria.where(StoredBookmark::createTime.name)
                .`is`(lastCreateTime).and(
                    Criteria.where(StoredBookmark::id.name).greaterThan(lastId)
                )
        val bookmarkCriteria = Criteria.where(StoredBookmark::createTime.name)
            .lessThan(lastCreateTime)
            .or(createTimeEqualCriteria)

        return r2dbcEntityTemplate.select(
            Query.query(getUserCriteria(userId).and(bookmarkCriteria)).sort(
                Sort.by(
                    listOf(
                        Sort.Order(
                            Sort.Direction.DESC,
                            StoredBookmark::createTime.name
                        ),
                        Sort.Order(Sort.Direction.ASC, StoredBookmark::id.name)
                    )
                )
            ).limit(pageSize),
            StoredBookmark::class.java
        ).collectList()
    }

    fun getUserCriteria(userId: String): Criteria {
        return Criteria.where(StoredBookmark::userId.name).`is`(userId)
    }
}
