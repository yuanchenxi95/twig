package com.yuanchenxi95.twig.models

import org.springframework.data.annotation.Id
import java.time.Instant

data class StoredSession(
    @Id val id: String,
    val userId: String,
    val expirationTime: Instant,
    val createTime: Instant? = null,
)
