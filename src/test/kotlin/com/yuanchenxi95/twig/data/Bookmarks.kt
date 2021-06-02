package com.yuanchenxi95.twig.data

import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark

val STORED_BOOKMARK_1 = StoredBookmark(id = "1", urlId = "1", userId = "1")
val STORED_BOOKMARK_2 = StoredBookmark(id = "2", urlId = "2", userId = "2")

val API_BOOKMARK_1: Bookmark = Bookmark.newBuilder().setId("1")
    .setUrl("http://example.com/foo")
    .build()
val API_BOOKMARK_2: Bookmark = Bookmark.newBuilder().setId("2")
    .setUrl("https://example.com/bar")
    .build()
