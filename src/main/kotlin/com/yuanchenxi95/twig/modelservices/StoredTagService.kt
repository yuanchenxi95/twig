package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.utils.databaseutils.selectList
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StoredTagService {

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var uuidUtils: UuidUtils

    fun queryTagsForUserByTagNames(
        userId: String,
        tagNames: Iterable<String>,
    ): Mono<List<StoredTag>> {
        val tagNamesCriteria = tagNames.map { Criteria.where(StoredTag::tagName.name).`is`(it) }
        return selectList(getUserCriteria(userId), tagNamesCriteria, r2dbcEntityTemplate)
    }

    fun queryTagsForUserByTagIds(
        userId: String,
        tagIds: Iterable<String>,
    ): Mono<List<StoredTag>> {
        val tagIdsCriteria = tagIds.map { Criteria.where(StoredTag::id.name).`is`(it) }
        return selectList(getUserCriteria(userId), tagIdsCriteria, r2dbcEntityTemplate)
    }

    fun queryTagsForBookmark(
        userId: String,
        bookmarkId: String,
    ): Mono<List<StoredTag>> {
        val bookmarkIdCriteria =
            Criteria.where(StoredTagsBookmarks::bookmarkId.name).`is`(bookmarkId)
        val storedTagsBookmarks = r2dbcEntityTemplate.select(
            Query.query(bookmarkIdCriteria),
            StoredTagsBookmarks::class.java
        )
        return storedTagsBookmarks.map { it.tagId }.collectList()
            .flatMap {
                queryTagsForUserByTagIds(userId, it)
            }
    }

    fun batchCreateTags(userId: String, tagNames: Iterable<String>): Mono<List<StoredTag>> {
        val newStoredTags = tagNames.map {
            StoredTag(
                id = uuidUtils.generateUUID(),
                tagName = it,
                userId = userId,
            )
        }
        val insertTagsQuery = newStoredTags.map {
            r2dbcEntityTemplate.insert(it)
        }

        val newStoredTagIds = newStoredTags.map { it.id }
        val queryForTagsMono = queryTagsForUserByTagIds(
            userId,
            newStoredTagIds,
        )
        return parallelExecuteWithLimit(insertTagsQuery)
            .then(queryForTagsMono)
    }

    fun getUserCriteria(userId: String): Criteria {
        return Criteria.where(StoredTag::userId.name).`is`(userId)
    }
}
