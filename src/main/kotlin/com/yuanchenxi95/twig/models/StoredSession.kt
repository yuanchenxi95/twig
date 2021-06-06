package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("session")
data class StoredSession(
    @Id val id: String,
    val userId: String,
    val expirationTime: Instant,
    val createTime: Instant? = null,
)
