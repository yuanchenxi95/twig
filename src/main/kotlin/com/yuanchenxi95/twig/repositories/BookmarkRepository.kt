package com.yuanchenxi95.twig.repositories

import com.yuanchenxi95.twig.models.StoredBookmark
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface BookmarkRepository : ReactiveCrudRepository<StoredBookmark, String>
