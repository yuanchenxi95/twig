package com.yuanchenxi95.twig.repositories

import com.yuanchenxi95.twig.models.StoredTag
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TagRepository : ReactiveCrudRepository<StoredTag, String>
