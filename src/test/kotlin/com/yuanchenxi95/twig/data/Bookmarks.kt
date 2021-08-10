package com.yuanchenxi95.twig.data

import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark

val STORED_BOOKMARK_1 = StoredBookmark(id = "1", urlId = STORED_URL_1.id, userId = STORED_USER_1.id)
val STORED_BOOKMARK_2 = StoredBookmark(id = "2", urlId = STORED_URL_1.id, userId = STORED_USER_1.id)
val STORED_BOOKMARK_3 = StoredBookmark(id = "3", urlId = STORED_URL_1.id, userId = STORED_USER_2.id)
val STORED_BOOKMARK_4 = StoredBookmark(id = "4", urlId = STORED_URL_2.id, userId = STORED_USER_1.id)

val API_BOOKMARK_1: Bookmark = Bookmark.newBuilder().setId(STORED_BOOKMARK_1.id)
    .setUrl(STORED_URL_1.url)
    .build()
val API_BOOKMARK_2: Bookmark = Bookmark.newBuilder().setId("2")
    .setUrl("https://example.com/bar")
    .build()
val API_BOOKMARK_3: Bookmark = Bookmark.newBuilder().setId(STORED_BOOKMARK_4.id)
    .setUrl(STORED_URL_2.url)
    .build()
