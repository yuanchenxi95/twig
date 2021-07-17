package com.yuanchenxi95.twig.utils.databaseutils

import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import reactor.core.publisher.Mono
import java.lang.Exception
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

inline fun <reified T : Any> findIdKeyField(): String {
    val idField = T::class.declaredMemberProperties.find {
        it.javaField?.isAnnotationPresent(Id::class.java) ?: false
    }
    return idField!!.name
}

inline fun <reified T : Any> selectOneById(
    id: String,
    r2dbcEntityTemplate: R2dbcEntityTemplate,
    useDefaultID: Boolean = true
): Mono<T> {
    return try {
        val idFieldName = if (useDefaultID) "id" else findIdKeyField<T>()
        r2dbcEntityTemplate.selectOne(
            Query.query(Criteria.where(idFieldName).`is`(id)),
            T::class.java
        )
    } catch (exception: Exception) {
        Mono.error(exception)
    }
}
