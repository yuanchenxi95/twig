package com.yuanchenxi95.twig.data

import com.yuanchenxi95.twig.models.StoredSession
import java.time.Instant

val STORED_SESSION_1 = StoredSession(
    id = "00000000-0000-0000-0000-000000000000",
    userId = STORED_USER_1.id,
    expirationTime = Instant.now().plusSeconds(3600)
)
