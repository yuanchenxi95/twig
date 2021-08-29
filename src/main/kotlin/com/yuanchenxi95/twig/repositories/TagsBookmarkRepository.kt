package com.yuanchenxi95.twig.repositories

import com.yuanchenxi95.twig.models.StoredTagsBookmarks
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TagsBookmarkRepository : ReactiveCrudRepository<StoredTagsBookmarks, String>
