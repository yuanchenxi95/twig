package com.yuanchenxi95.twig.daos

import com.yuanchenxi95.twig.models.StoredBookmark
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface BookmarkRepository : ReactiveCrudRepository<StoredBookmark, Long> {
    @Query("SELECT * FROM bookmark WHERE hostname = :hostname")
    fun findByHostname(hostname: String): Flux<StoredBookmark>
}
