package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("bookmark")
data class StoredBookmark(
    @Id val id: String,
    val displayName: String,
    val urlId: String,
    val userId: String,
    val createTime: Instant? = null,
    val updateTime: Instant? = null,
)
