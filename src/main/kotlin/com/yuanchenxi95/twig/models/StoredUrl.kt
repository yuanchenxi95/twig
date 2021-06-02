package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("url")
data class StoredUrl(
    @Id val id: String,
    val protocol: String,
    val host: String,
    val path: String,
    val url: String,
    val createTime: Instant? = null,
    val updateTime: Instant? = null,
)
