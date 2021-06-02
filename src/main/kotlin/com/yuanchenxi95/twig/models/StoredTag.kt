package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("tag")
data class StoredTag(
    @Id
    val id: String,
    val tagName: String,
    @CreatedDate
    val createTime: Instant? = null,
)
