package com.yuanchenxi95.twig.repositories

import com.yuanchenxi95.twig.models.StoredUrl
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UrlRepository : ReactiveCrudRepository<StoredUrl, String>
