package com.yuanchenxi95.twig.constants

class RequestMappingValues {
    companion object {
        const val BOOKMARK_ID = "bookmarkId"

        const val CREATE_BOOKMARK = "/api/bookmarks"
        const val LIST_BOOKMARK = "/api/bookmarks"
        const val UPDATE_BOOKMARK = "/api/bookmarks/{$BOOKMARK_ID}"
        const val CREATE_TAG = "/api/tags"
        const val DELETE_TAG = "/api/tags/{tagName}"
        const val GET_USER_INFORMATION = "/api/users/me"
        const val LIST_TAG = "/api/tags"
    }
}
