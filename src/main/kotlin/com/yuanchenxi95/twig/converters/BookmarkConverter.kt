package com.yuanchenxi95.twig.converters

import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.models.StoredTag
import com.yuanchenxi95.twig.models.StoredUrl
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import org.springframework.stereotype.Component

@Component
class BookmarkConverter :
    TwigConverter<Triple<StoredBookmark, StoredUrl, List<StoredTag>>, Bookmark> {
    override fun doForward(source: Triple<StoredBookmark, StoredUrl, List<StoredTag>>): Bookmark {
        val (storedBookmark, storedUrl, storedTags) = source
        return Bookmark.newBuilder()
            .setId(storedBookmark.id)
            .setDisplayName(storedBookmark.displayName)
            .setUrl(storedUrl.url)
            .addAllTags(storedTags.map { it.tagName })
            .build()
    }

    override fun doBackward(target: Bookmark): Triple<StoredBookmark, StoredUrl, List<StoredTag>> {
        TODO("Not yet implemented")
    }
}
