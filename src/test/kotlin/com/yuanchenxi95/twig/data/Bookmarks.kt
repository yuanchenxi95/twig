package com.yuanchenxi95.twig.data

import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark

val STORED_BOOKMARK_1 = StoredBookmark(
    id = "1",
    displayName = "display name 1",
    urlId = STORED_URL_1.id,
    userId = STORED_USER_1.id
)
val STORED_BOOKMARK_2 = StoredBookmark(
    id = "2",
    displayName = "display name 1",
    urlId = STORED_URL_1.id,
    userId = STORED_USER_1.id
)
val STORED_BOOKMARK_3 = StoredBookmark(
    id = "3",
    displayName = "display name 1",
    urlId = STORED_URL_1.id,
    userId = STORED_USER_2.id
)
val STORED_BOOKMARK_4 = StoredBookmark(
    id = "4",
    displayName = "display name 1",
    urlId = STORED_URL_2.id,
    userId = STORED_USER_1.id
)

val API_BOOKMARK_1: Bookmark = Bookmark.newBuilder().setId(STORED_BOOKMARK_1.id)
    .setDisplayName(STORED_BOOKMARK_1.displayName)
    .setUrl(STORED_URL_1.url)
    .build()
val API_BOOKMARK_2: Bookmark = Bookmark.newBuilder().setId("2")
    .setDisplayName(STORED_BOOKMARK_2.displayName)
    .setUrl("https://example.com/bar")
    .build()
val API_BOOKMARK_3: Bookmark = Bookmark.newBuilder().setId(STORED_BOOKMARK_4.id)
    .setDisplayName(STORED_BOOKMARK_2.displayName)
    .setUrl(STORED_URL_2.url)
    .build()
