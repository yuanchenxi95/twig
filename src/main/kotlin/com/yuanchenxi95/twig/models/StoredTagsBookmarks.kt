package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("tags_bookmarks")
data class StoredTagsBookmarks(
    @Id() val id: String,
    val bookmarkId: String,
    val tagId: String,
    val createTime: Instant? = null,
)
