package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("tag")
data class StoredTag(
    @Id
    val id: String,
    val tagName: String,
    val userId: String,
    @CreatedDate
    val createTime: Instant? = null,
)
