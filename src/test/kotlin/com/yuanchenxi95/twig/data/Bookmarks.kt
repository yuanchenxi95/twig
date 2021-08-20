package com.yuanchenxi95.twig.data

import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark
import com.yuanchenxi95.twig.protobuf.api.bookmark

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

val API_BOOKMARK_1: Bookmark =
    bookmark {
        id = STORED_BOOKMARK_1.id
        displayName = STORED_BOOKMARK_1.displayName
        url = STORED_URL_1.url
    }

val API_BOOKMARK_2: Bookmark = bookmark {
    id = "2"
    displayName = STORED_BOOKMARK_2.displayName
    url = "https://example.com/bar"
}

val API_BOOKMARK_3: Bookmark = bookmark {
    id = STORED_BOOKMARK_4.id
    displayName = STORED_BOOKMARK_2.displayName
    url = STORED_URL_2.url
}
