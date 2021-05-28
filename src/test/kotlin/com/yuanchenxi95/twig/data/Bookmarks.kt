package com.yuanchenxi95.twig.data

import com.yuanchenxi95.twig.models.StoredBookmark
import com.yuanchenxi95.twig.protobuf.api.Bookmark

val STORED_BOOKMARK_1 = StoredBookmark(id = 1, hostname = "example.com", uri = "foo", protocol = "http")
val STORED_BOOKMARK_2 = StoredBookmark(id = 2, hostname = "example.com", uri = "bar", protocol = "https")

val API_BOOKMARK_1: Bookmark = Bookmark.newBuilder().setId(1).setHostname("example.com")
    .setUri("foo")
    .setProtocol("http")
    .build()
val API_BOOKMARK_2: Bookmark = Bookmark.newBuilder().setId(2).setHostname("example.com")
    .setUri("bar")
    .setProtocol("https")
    .build()
