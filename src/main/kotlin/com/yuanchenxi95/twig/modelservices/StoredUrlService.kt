package com.yuanchenxi95.twig.modelservices

import com.yuanchenxi95.twig.models.StoredUrl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StoredUrlService {
    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    fun selectOneUrlById(urlId: String): Mono<StoredUrl> {
        val urlQuery = Criteria.where(StoredUrl::id.name).`is`(urlId)
        return r2dbcEntityTemplate.selectOne(Query.query(urlQuery), StoredUrl::class.java)
    }
}
