package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("tags_users")
data class StoredTagsUsers(
    @Id() val id: String,
    val userId: String,
    val tagId: String,
    val createTime: Instant? = null,
)
