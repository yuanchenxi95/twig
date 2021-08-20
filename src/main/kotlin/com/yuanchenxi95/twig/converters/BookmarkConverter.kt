package com.yuanchenxi95.twig.converters

import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.models.StoredUrl
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.bookmark
import org.springframework.stereotype.Component

@Component
class BookmarkConverter :
    TwigConverter<Triple<StoredBookmark, StoredUrl, List<StoredTag>>, Bookmark> {
    override fun doForward(source: Triple<StoredBookmark, StoredUrl, List<StoredTag>>): Bookmark {
        val (storedBookmark, storedUrl, storedTags) = source
        return bookmark {
            id = storedBookmark.id
            displayName = storedBookmark.displayName
            url = storedUrl.url
            tags.addAll(storedTags.map { it.tagName })
        }
    }

    override fun doBackward(target: Bookmark): Triple<StoredBookmark, StoredUrl, List<StoredTag>> {
        TODO("Not yet implemented")
    }
}
