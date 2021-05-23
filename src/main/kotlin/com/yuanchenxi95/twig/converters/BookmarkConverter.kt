package com.yuanchenxi95.twig.converters

import com.google.common.base.Converter
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import org.springframework.stereotype.Component

@Component
class BookmarkConverter : Converter<Bookmark, StoredBookmark>() {
    override fun doForward(bookmark: Bookmark): StoredBookmark {
        return StoredBookmark(
            hostname = bookmark.hostname,
            uri = bookmark.uri,
            protocol = bookmark.protocol
        )
    }

    override fun doBackward(storedBookmark: StoredBookmark): Bookmark {
        return Bookmark.newBuilder().setId(storedBookmark.id!!).setHostname(storedBookmark.hostname)
            .setUri(storedBookmark.uri).setProtocol(storedBookmark.protocol).build()
    }
}
