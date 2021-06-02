package com.yuanchenxi95.twig.converters

import com.google.common.base.Converter
import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import org.springframework.stereotype.Component

@Component
class BookmarkConverter : Converter<Bookmark, StoredBookmark>() {
    override fun doForward(a: Bookmark): StoredBookmark {
        TODO("Not yet implemented")
    }

    override fun doBackward(b: StoredBookmark): Bookmark {
        TODO("Not yet implemented")
    }
}
