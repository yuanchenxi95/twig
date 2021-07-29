package com.yuanchenxi95.twig.streams

import com.yuanchenxi95.twig.models.StoredUrl
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Service

@Service
class UrlStreamConsumer : StreamListener<String, ObjectRecord<String, StoredUrl>> {

    override fun onMessage(message: ObjectRecord<String, StoredUrl>) {
        // TODO("Use logger instead of println")
        println("url:" + message.value.url)
        // TODO("Add more logic")
    }
}
