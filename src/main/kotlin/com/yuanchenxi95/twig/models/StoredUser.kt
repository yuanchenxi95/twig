package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("user")
data class StoredUser(
    @Id val id: String,
    val userEmail: String,
    val createTime: Instant? = null,
    val updateTime: Instant? = null,
)
