package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import com.yuanchenxi95.twig.utils.databaseutils.deleteList
import com.yuanchenxi95.twig.utils.reactorutils.parallelExecuteWithLimitOrderedArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StoredTagsBookmarksService {
    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @Autowired
    lateinit var uuidUtils: UuidUtils

    fun batchCreateReferences(
        bookmarkId: String,
        tagIds: Iterable<String>
    ): Mono<List<StoredTagsBookmarks>> {
        val createTagsBookmarksMono = tagIds.map {
            r2dbcEntityTemplate.insert(
                StoredTagsBookmarks(
                    id = uuidUtils.generateUUID(),
                    bookmarkId = bookmarkId,
                    tagId = it
                )
            )
        }

        return parallelExecuteWithLimitOrderedArray(createTagsBookmarksMono)
    }

    fun batchDeleteReferences(bookmarkId: String, tagIds: Iterable<String>): Mono<Int> {
        val idsCriteria = tagIds.map { Criteria.where(StoredTagsBookmarks::tagId.name).`is`(it) }
        return deleteList<StoredTagsBookmarks>(Criteria.empty(), idsCriteria, r2dbcEntityTemplate)
    }
}
